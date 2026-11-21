#!/usr/bin/env python3
"""Строит PNG-изображения из моделей SysML v2 (текстовая нотация).

Поддерживается подмножество языка, использованное в srs/models:
  * мокапы окон настольного приложения — дерево частей (part) на элементах
    библиотеки UiKit;
  * контекстная диаграмма — части и соединения (connection ... connect a to b);
  * карта экранов — state def с состояниями и переходами (transition).

Запуск из корня репозитория:
    python srs/tools/sysml2png.py

Зависимости: Pillow (pip install pillow); для диаграмм — Graphviz (dot).
"""
import re
import subprocess
import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parent.parent
MODELS = ROOT / "models"
IMAGES = ROOT / "images"

# ----------------------------------------------------------------- разбор

TOKEN = re.compile(r'''
    (?P<doc>/\*.*?\*/) | (?P<line>//[^\n]*) |
    (?P<str>"(?:[^"\\]|\\.)*") |
    (?P<sym>:>>|:>|::|\.\.|[{};:=(),\[\]*]) |
    (?P<word>[^\s{};:=(),\[\]*"]+)
''', re.S | re.X)


def tokenize(text):
    out = []
    for m in TOKEN.finditer(text):
        kind = m.lastgroup
        if kind == "line":
            continue
        out.append((kind, m.group()))
    return out


class Node:
    def __init__(self, header, doc=""):
        self.header = header          # список лексем до '{' или ';'
        self.children = []
        self.doc = doc

    @property
    def words(self):
        return [v for _, v in self.header]

    def kind(self):
        w = self.words
        if not w:
            return ""
        if len(w) > 1 and w[1] == "def":
            return w[0] + " def"
        return w[0]

    def name(self):
        w = self.words
        skip = 2 if self.kind().endswith("def") else 1
        return w[skip] if len(w) > skip and re.match(r"\w", w[skip]) else ""

    def type(self):
        w = self.words
        return w[w.index(":") + 1].split("::")[-1] if ":" in w else ""

    def parts(self):
        return [c for c in self.children if c.kind() == "part"]

    def attrs(self):
        """Значения переопределённых атрибутов: attribute :>> имя = значение;"""
        res = {}
        for c in self.children:
            w = c.words
            if c.kind() == "attribute" and ":>>" in w and "=" in w:
                key = w[w.index(":>>") + 1]
                vals = [unquote(v) if k == "str" else v.split("::")[-1]
                        for k, v in c.header[w.index("=") + 1:] if v not in ("(", ")", ",", "::")]
                res[key] = vals if "(" in w else vals[-1]
        return res


def unquote(s):
    return re.sub(r'\\(.)', r'\1', s[1:-1])


def parse(tokens):
    root = Node([])
    stack = [root]
    header = []
    for kind, val in tokens:
        if kind == "doc":
            text = re.sub(r"^/\*+|\*+/$", "", val).strip()
            if header and header[-1][1] == "doc":
                header.pop()
                stack[-1].doc = text
            continue
        if val == "{":
            node = Node(header)
            stack[-1].children.append(node)
            stack.append(node)
            header = []
        elif val == "}":
            stack.pop()
            header = []
        elif val == ";":
            if header:
                stack[-1].children.append(Node(header))
            header = []
        else:
            header.append((kind, val))
    return root


def load(name):
    return parse(tokenize((MODELS / name).read_text(encoding="utf-8")))


def walk(node):
    yield node
    for c in node.children:
        yield from walk(c)


# ----------------------------------------------------------------- мокапы

def font(size, bold=False):
    names = (["DejaVuSans-Bold.ttf", "arialbd.ttf"] if bold
             else ["DejaVuSans.ttf", "arial.ttf"])
    for n in names:
        try:
            return ImageFont.truetype(n, size)
        except OSError:
            pass
    return ImageFont.load_default()


F, FB, FS, FH, FBIG = font(15), font(15, True), font(13), font(19, True), font(24, True)
INK, MUTED, LINE, FILL, ACCENT, DARK = "#1f2933", "#7b8794", "#9aa5b1", "#f5f7fa", "#2f6fde", "#323f4b"
WARN, SELECT, GOOD, MID, BAD = "#c2410c", "#dbe7fb", "#2f9e44", "#e8a317", "#d64545"
GAP, PAD, W = 14, 16, 1280
CONTAINERS = {"Screen", "Row", "Column", "Panel"}
FIELDS = ("TextField", "PasswordField", "Select", "SpinBox", "TextArea")


def fit(d, text, f, width):
    if d.textlength(text, font=f) <= width:
        return text
    while text and d.textlength(text + "…", font=f) > width:
        text = text[:-1]
        if not text:
            return ""
    return text + "…"


def horizontal(node):
    return node.type() == "Row" or node.attrs().get("orientation") == "horizontal"


def height(node, width):
    t, a = node.type(), node.attrs()
    if t in CONTAINERS:
        kids = node.parts()
        inner = width - (2 * PAD if t in ("Panel", "Screen") else 0)
        if horizontal(node):
            h = max([height(k, w) for k, w in zip(kids, split(kids, inner))], default=0)
        else:
            h = sum(height(k, inner) for k in kids) + GAP * max(len(kids) - 1, 0)
        if t == "Panel":
            h += 2 * PAD + (34 if a.get("text") else 0)
        return h
    if t == "MenuBar":
        return 30
    if t == "StatusBar":
        return 30
    if t == "ListView":
        return 36 * len(a.get("items", [])) + 8
    if t in FIELDS:
        return (86 if t == "TextArea" else 38) + (22 if a.get("label") else 0)
    if t == "Slider":
        return 22 + 30
    if t == "Meter":
        return 22 + 14
    if t == "Button":
        return 38
    if t == "StatCard":
        return 86
    if t == "Table":
        return 36 * (1 + len(node.parts()))
    return 24  # Label, Link, Checkbox


def split(kids, width):
    weights = [int(k.attrs().get("weight", 1)) for k in kids]
    free = width - GAP * (len(kids) - 1)
    return [free * w // sum(weights) for w in weights]


def draw(d, node, x, y, w):
    t, a = node.type(), node.attrs()
    text = a.get("text", "")
    h = height(node, w)
    if t in CONTAINERS:
        kids = node.parts()
        if t == "Panel":
            d.rounded_rectangle([x, y, x + w, y + h], 8, fill="white", outline=LINE)
            x, y, w = x + PAD, y + PAD, w - 2 * PAD
            if text:
                d.text((x, y), text, font=FH, fill=INK)
                y += 34
        if horizontal(node):
            for k, kw in zip(kids, split(kids, w)):
                draw(d, k, x, y, kw)
                x += kw + GAP
        else:
            for k in kids:
                draw(d, k, x, y, w)
                y += height(k, w) + GAP
    elif t == "MenuBar":
        d.rectangle([x, y, x + w, y + h], fill=FILL)
        d.line([x, y + h, x + w, y + h], fill=LINE)
        cx = x + PAD
        for item in a.get("items", []):
            d.text((cx, y + 6), item, font=F, fill=INK)
            cx += d.textlength(item, font=F) + 26
    elif t == "StatusBar":
        d.rectangle([x, y, x + w, y + h], fill=FILL)
        d.line([x, y, x + w, y], fill=LINE)
        d.text((x + PAD, y + 7), fit(d, text, FS, w - 2 * PAD), font=FS, fill=MUTED)
    elif t == "ListView":
        d.rounded_rectangle([x, y, x + w, y + h], 6, fill=FILL, outline=LINE)
        for i, item in enumerate(a.get("items", [])):
            iy = y + 4 + 36 * i
            if item == a.get("active"):
                d.rounded_rectangle([x + 4, iy, x + w - 4, iy + 32], 5, fill=SELECT)
            f = FB if item == a.get("active") else F
            d.text((x + 14, iy + 7), fit(d, item, f, w - 28), font=f, fill=INK)
    elif t in FIELDS:
        if a.get("label"):
            d.text((x, y), a["label"], font=FS, fill=INK)
            y += 22
        fh = 86 if t == "TextArea" else 38
        d.rounded_rectangle([x, y, x + w, y + fh], 5, fill="white", outline=LINE)
        acts = a.get("actions", [])
        if isinstance(acts, str):
            acts = [acts]
        mark = {"Select": "▾", "SpinBox": "⇅"}.get(t)
        right = x + w - (28 if mark else 6)
        for act in reversed(acts):
            bw = d.textlength(act, font=FS) + 16
            d.rounded_rectangle([right - bw, y + 6, right, y + 32], 4, fill=FILL, outline=LINE)
            d.text((right - bw + 8, y + 10), act, font=FS, fill=ACCENT)
            right -= bw + 6
        d.text((x + 10, y + 10), fit(d, text, F, right - x - 16), font=F,
               fill=INK if text and t != "PasswordField" or t == "SpinBox" else MUTED)
        if mark:
            d.text((x + w - 24, y + 9), mark, font=F, fill=INK)
    elif t == "Slider":
        d.text((x, y), a.get("label", ""), font=FS, fill=INK)
        lo, hi, v = int(a.get("min", 0)), int(a.get("max", 100)), int(a.get("value", 0))
        ty = y + 22 + 15
        d.line([x + 8, ty, x + w - 60, ty], fill=LINE, width=4)
        kx = x + 8 + (w - 68) * (v - lo) / max(hi - lo, 1)
        d.line([x + 8, ty, kx, ty], fill=ACCENT, width=4)
        d.ellipse([kx - 9, ty - 9, kx + 9, ty + 9], fill="white", outline=ACCENT, width=3)
        d.text((x + w - 48, ty - 9), f"{lo}–{hi}", font=FS, fill=MUTED)
    elif t == "Meter":
        v = int(a.get("value", 0))
        col = BAD if v < 40 else MID if v < 70 else GOOD
        d.text((x, y), a.get("label", ""), font=FS, fill=INK)
        by = y + 22
        d.rounded_rectangle([x, by, x + w, by + 10], 5, fill="#e4e7eb")
        if v:
            d.rounded_rectangle([x, by, x + max(w * v // 100, 10), by + 10], 5, fill=col)
    elif t == "Button":
        primary = a.get("primary") == "true"
        d.rounded_rectangle([x, y, x + w, y + 38], 6, fill=ACCENT if primary else "white",
                            outline=ACCENT if primary else INK)
        label = fit(d, text, FB, w - 12)
        d.text((x + (w - d.textlength(label, font=FB)) / 2, y + 10), label, font=FB,
               fill="white" if primary else INK)
    elif t == "Checkbox":
        d.rectangle([x, y + 3, x + 16, y + 19], outline=INK, fill="white")
        if a.get("checked") == "true":
            d.line([x + 3, y + 11, x + 7, y + 15, x + 13, y + 6], fill=ACCENT, width=3)
        d.text((x + 26, y + 2), fit(d, text, F, w - 26), font=F, fill=INK)
    elif t == "StatCard":
        d.rounded_rectangle([x, y, x + w, y + h], 8, fill=FILL, outline=LINE)
        d.text((x + PAD, y + 14), fit(d, a.get("label", ""), FS, w - 2 * PAD), font=FS, fill=MUTED)
        d.text((x + PAD, y + 40), fit(d, text, FBIG, w - 2 * PAD), font=FBIG, fill=INK)
    elif t == "Table":
        cols = a.get("columns", [])
        d.rectangle([x, y, x + w, y + 36], fill=FILL)
        rows = [cols] + [r.attrs().get("cells", []) for r in node.parts()]
        # ширина колонок пропорциональна самому длинному значению
        need = [max(d.textlength(r[j], font=FB if i == 0 else FS) for i, r in enumerate(rows) if j < len(r)) + 22
                for j in range(len(cols))]
        cws = [n * w / sum(need) for n in need]
        sel = [False] + [r.attrs().get("selected") == "true" for r in node.parts()]
        for i, row in enumerate(rows):
            ry = y + 36 * i
            cx = x
            if sel[i]:
                d.rectangle([x + 1, ry + 1, x + w - 1, ry + 35], fill=SELECT)
            for cell, cw in zip(row, cws):
                d.text((cx + 8, ry + 10), fit(d, cell, FB if i == 0 else FS, cw - 14),
                       font=FB if i == 0 else FS, fill=INK)
                cx += cw
            d.line([x, ry + 36, x + w, ry + 36], fill=LINE)
        d.rectangle([x, y, x + w, y + 36 * len(rows)], outline=LINE)
    else:  # Label, Link
        style = a.get("style", "normal")
        f, col = {"heading": (FH, INK), "muted": (FS, MUTED), "warning": (FS, WARN)}.get(style, (F, INK))
        d.text((x, y + 3), fit(d, text, f, w), font=f, fill=ACCENT if t == "Link" else col)


def render_screen(node, path):
    """Окно настольного приложения: заголовок с кнопками управления окном."""
    a = node.attrs()
    width = int(a.get("width", W))
    chrome = 36
    body = height(node, width) + 2 * PAD
    img = Image.new("RGB", (width, chrome + body + 60), "white")
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, width, chrome], fill="#e4e7eb")
    d.rounded_rectangle([12, 9, 30, 27], 4, fill=ACCENT)                 # значок приложения
    d.text((17, 9), "K", font=FB, fill="white")
    d.text((42, 9), a.get("title", ""), font=F, fill=INK)
    buttons = ("✕",) if a.get("modal") == "true" else ("—", "☐", "✕")
    for i, b in enumerate(reversed(buttons)):
        bx = width - 46 * (i + 1)
        d.text((bx + 16, 8), b, font=F, fill=INK)
    kids = node.parts()
    y = chrome
    if kids and kids[0].type() == "MenuBar":     # меню — во всю ширину окна
        draw(d, kids[0], 0, y, width)
        y += height(kids[0], width) + PAD
        kids = kids[1:]
    else:
        y += PAD
    status = None
    if kids and kids[-1].type() == "StatusBar":  # строка состояния — внизу во всю ширину
        status, kids = kids[-1], kids[:-1]
    for k in kids:
        draw(d, k, PAD, y, width - 2 * PAD)
        y += height(k, width - 2 * PAD) + GAP
    y += PAD - GAP
    if status:
        draw(d, status, 0, y, width)
        y += height(status, width)
    img = img.crop((0, 0, width, y))
    ImageDraw.Draw(img).rectangle([0, 0, width - 1, img.height - 1], outline=LINE)
    img.save(path)


# ----------------------------------------------------------------- диаграммы

def dot(src, path):
    subprocess.run(["dot", "-Tpng", "-Gdpi=130", "-o", str(path)], input=src.encode("utf-8"), check=True)


BASE = ('graph [fontname="DejaVu Sans" bgcolor=white pad=0.3];\n'
        'node [fontname="DejaVu Sans" fontsize=12 style="rounded,filled" fillcolor="#f5f7fa" color="#52606d"];\n'
        'edge [fontname="DejaVu Sans" fontsize=10 color="#52606d" fontcolor="#323f4b"];\n')


def render_context(root, path):
    ctx = next(n for n in walk(root) if n.kind() == "part" and n.name() == "systemContext")
    shapes = {"Person": "shape=oval", "EncryptedFile": "shape=note", "Folder": "shape=folder",
              "OperatingSystem": "shape=box3d", "ExternalService": 'shape=box style="rounded,dashed,filled"'}
    main = "keyNest"
    out = ["digraph context {", "rankdir=LR; nodesep=0.35; ranksep=0.7;", BASE]
    for p in ctx.parts():
        shape = 'shape=box penwidth=2 fillcolor="#dbe7fb"' if p.name() == main else shapes.get(p.type(), "shape=box")
        out.append(f'{p.name()} [label="{p.doc}\\n«{p.type()}»" {shape}];')
    for c in ctx.children:
        w = c.words
        if c.kind() == "connection":
            src, dst = w[w.index("connect") + 1], w[w.index("to") + 1]
            out.append(f'{src} -> {dst} [label="{c.doc}" dir=none];')
            if main not in (src, dst):          # связь двух внешних элементов — в одной колонке
                out.append(f'{{rank=same; {src}; {dst};}}')
    out.append("}")
    dot("\n".join(out), path)


def render_flow(root, path):
    flow = next(n for n in walk(root) if n.kind() == "state def")
    out = ["digraph flow {", "rankdir=LR; nodesep=0.5; ranksep=0.9;", BASE,
           'start [shape=circle label="" width=0.22 fillcolor="#323f4b"];']
    first = None
    for c in flow.children:
        w = c.words
        if c.kind() == "then" and first is None:
            first = w[1]
        elif c.kind() == "state":
            out.append(f'{c.name()} [shape=box label="{c.doc}\\n{c.name()}"];')
        elif c.kind() == "transition":
            out.append(f'{w[w.index("first") + 1]} -> {w[w.index("then") + 1]} [label="{w[w.index("accept") + 1]}"];')
    out.append(f"start -> {first};")
    out.append("}")
    dot("\n".join(out), path)


def main():
    IMAGES.mkdir(exist_ok=True)
    mock = load("Mockups.sysml")
    screens = [n for n in walk(mock) if n.kind() == "part" and n.type() == "Screen"]
    for i, s in enumerate(screens, 1):
        name = re.sub(r"Screen$", "", s.name())
        name = re.sub(r"(?<!^)([A-Z])", r"-\1", name).lower()
        target = IMAGES / f"mockup-{i:02d}-{name}.png"
        render_screen(s, target)
        print("создан", target.relative_to(ROOT.parent))
    ctx = load("Context.sysml")
    for fn, target in ((render_context, "context.png"), (render_flow, "screen-flow.png")):
        try:
            fn(ctx, IMAGES / target)
            print("создан", (IMAGES / target).relative_to(ROOT.parent))
        except FileNotFoundError:
            print("Graphviz (dot) не найден — пропущено:", target, file=sys.stderr)


if __name__ == "__main__":
    main()
