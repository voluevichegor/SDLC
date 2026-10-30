package model;

/**
 * Единицы измерения длины, которые поддерживает программа.
 * Каждая единица хранит свой размер в метрах — через метр выполняется любой перевод.
 *
 * Размеры «необычных» единиц — усреднённые справочные значения.
 */
public enum LengthUnit {
    INCH("Дюйм", 0.0254, "точно 2,54 см"),
    YARD("Ярд", 0.9144, "точно 91,44 см"),
    CENTIMETER("Сантиметр", 0.01, "1/100 метра"),
    METER("Метр", 1.0, "базовая единица СИ"),
    AMERICAN_COCKROACH("Американский таракан", 0.04,
            "средняя длина взрослого Periplaneta americana"),
    GIRAFFE_NECK("Шея жирафа", 1.8,
            "средняя длина шеи взрослого жирафа"),
    LONGEST_SNAKE("Самая длинная змея", 7.67,
            "рекордный сетчатый питон, Книга рекордов Гиннесса"),
    HUMAN_TONGUE("Человеческий язык", 0.10,
            "средняя длина языка взрослого человека"),
    FOOTBALL_FIELD("Футбольное поле", 105.0,
            "стандартная длина поля по рекомендации FIFA");

    private final String displayName;
    private final double meters;
    private final String note;

    LengthUnit(String displayName, double meters, String note) {
        this.displayName = displayName;
        this.meters = meters;
        this.note = note;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Сколько метров содержит одна такая единица. */
    public double getMeters() {
        return meters;
    }

    /** Откуда взят размер единицы — показывается пользователю как подсказка. */
    public String getNote() {
        return note;
    }

    /** Используется выпадающими списками Swing для отображения элемента. */
    @Override
    public String toString() {
        return displayName;
    }
}
