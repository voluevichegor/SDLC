/**
 * Мокапы окон настольного приложения KeyNest (менеджер паролей, C++ / Qt 6).
 * Каждое окно — отдельный пакет с одной частью типа UiKit::Screen.
 * PNG-изображения строятся из этой модели: srs/tools/sysml2png.py.
 * Все данные на мокапах вымышленные.
 */
package KeyNestMockups {

    /** Окно 1. Разблокировка и создание хранилища (REQ-FUNC-001 … REQ-FUNC-003). */
    package UnlockMockup {
        private import UiKit::*;

        part unlockScreen : Screen {
            attribute :>> title = "KeyNest — разблокировка";
            attribute :>> width = 1100;

            part body : Row {
                part openForm : Panel {
                    attribute :>> text = "Открыть хранилище";
                    part vault : Select {
                        attribute :>> label = "Хранилище";
                        attribute :>> text = "C:\\Users\\student\\OneDrive\\KeyNest\\Личное.knv";
                    }
                    part master : PasswordField {
                        attribute :>> label = "Мастер-пароль";
                        attribute :>> text = "••••••••••••••••";
                        attribute :>> actions = ("Показать");
                    }
                    part buttons : Row {
                        part other : Button {
                            attribute :>> text = "Другой файл…";
                        }
                        part unlock : Button {
                            attribute :>> text = "Разблокировать";
                            attribute :>> primary = true;
                        }
                    }
                    part error : Label {
                        attribute :>> text = "Неверный мастер-пароль. Следующая попытка через 2 с";
                        attribute :>> style = TextStyle::warning;
                    }
                    part hint : Label {
                        attribute :>> text = "Ctrl+O — открыть файл, Enter — разблокировать";
                        attribute :>> style = TextStyle::muted;
                    }
                }
                part createForm : Panel {
                    attribute :>> text = "Новое хранилище";
                    part file : TextField {
                        attribute :>> label = "Файл хранилища";
                        attribute :>> text = "C:\\Users\\student\\Documents\\Личное.knv";
                        attribute :>> actions = ("Обзор…");
                    }
                    part master : PasswordField {
                        attribute :>> label = "Мастер-пароль (не менее 12 символов)";
                        attribute :>> text = "••••••••••••••••••";
                    }
                    part masterRepeat : PasswordField {
                        attribute :>> label = "Повтор мастер-пароля";
                    }
                    part strength : Meter {
                        attribute :>> label = "Стойкость: хорошая (≈ 72 бит)";
                        attribute :>> value = 70;
                    }
                    part warning : Label {
                        attribute :>> text = "Мастер-пароль невозможно восстановить — запишите его";
                        attribute :>> style = TextStyle::warning;
                    }
                    part create : Button {
                        attribute :>> text = "Создать хранилище";
                    }
                }
            }
        }
    }

    /** Окно 2. Главное окно (REQ-FUNC-004 … REQ-FUNC-010, REQ-FUNC-014, REQ-SEC-004, REQ-SEC-005). */
    package MainWindowMockup {
        private import UiKit::*;

        part mainWindowScreen : Screen {
            attribute :>> title = "Личное.knv — KeyNest";

            part menu : MenuBar {
                attribute :>> items = ("Файл", "Правка", "Вид", "Сервис", "Справка");
            }
            part toolbar : Row {
                part search : TextField {
                    attribute :>> text = "Поиск по названию, логину, URL и тегам (Ctrl+F)";
                    attribute :>> weight = 5;
                }
                part add : Button {
                    attribute :>> text = "+ Новая запись";
                    attribute :>> primary = true;
                }
                part generator : Button {
                    attribute :>> text = "Генератор";
                }
                part lock : Button {
                    attribute :>> text = "Заблокировать";
                }
            }
            part content : Row {
                part folders : ListView {
                    attribute :>> items = ("Все записи · 48", "Избранное · 7", "Работа · 15", "Учёба · 12",
                        "Финансы · 6", "Соцсети · 8", "Заметки · 4", "Корзина · 2", "Аудит · 5 проблем");
                    attribute :>> active = "Учёба · 12";
                    attribute :>> weight = 2;
                }
                part list : Table {
                    attribute :>> columns = ("Название", "Логин", "Изменено");
                    attribute :>> weight = 4;
                    part r1 : TableRow {
                        attribute :>> cells = ("★ GitHub", "voluevichegor", "02.11.2026");
                        attribute :>> selected = true;
                    }
                    part r2 : TableRow {
                        attribute :>> cells = ("ИИС «БГУИР»", "434701_student", "01.09.2026");
                    }
                    part r3 : TableRow {
                        attribute :>> cells = ("Moodle LMS", "student@bsuir.by", "14.03.2026");
                    }
                    part r4 : TableRow {
                        attribute :>> cells = ("Overleaf", "student@bsuir.by", "20.10.2025");
                    }
                    part r5 : TableRow {
                        attribute :>> cells = ("Stepik", "student@example.com", "05.05.2025");
                    }
                    part r6 : TableRow {
                        attribute :>> cells = ("Zoom", "student@example.com", "11.02.2025");
                    }
                }
                part details : Panel {
                    attribute :>> text = "GitHub";
                    attribute :>> weight = 4;
                    part login : TextField {
                        attribute :>> label = "Логин";
                        attribute :>> text = "voluevichegor";
                    }
                    part password : PasswordField {
                        attribute :>> label = "Пароль";
                        attribute :>> text = "••••••••••••••••••••";
                        attribute :>> actions = ("Показать", "Копировать");
                    }
                    part url : TextField {
                        attribute :>> label = "URL";
                        attribute :>> text = "https://github.com/login";
                        attribute :>> actions = ("Открыть");
                    }
                    part totp : TextField {
                        attribute :>> label = "Код 2FA (TOTP)";
                        attribute :>> text = "482 913";
                        attribute :>> actions = ("Копировать");
                    }
                    part totpTimer : Meter {
                        attribute :>> label = "Код сменится через 18 с";
                        attribute :>> value = 60;
                    }
                    part meta : Label {
                        attribute :>> text = "Папка: Учёба · Теги: git, учёба · Пароль изменён 02.11.2026";
                        attribute :>> style = TextStyle::muted;
                    }
                    part actions : Row {
                        part edit : Button {
                            attribute :>> text = "Изменить";
                        }
                        part history : Button {
                            attribute :>> text = "История";
                        }
                        part delete : Button {
                            attribute :>> text = "В корзину";
                        }
                    }
                }
            }
            part status : StatusBar {
                attribute :>> text = "Пароль скопирован — буфер обмена очистится через 30 с · Автоблокировка через 5 мин · Сохранено 11:42";
            }
        }
    }

    /** Окно 3. Добавление и изменение записи (REQ-FUNC-004, REQ-FUNC-006, REQ-FUNC-011, REQ-FUNC-012, REQ-FUNC-014). */
    package EntryEditorMockup {
        private import UiKit::*;

        part entryEditorScreen : Screen {
            attribute :>> title = "Изменение записи — KeyNest";
            attribute :>> width = 1000;
            attribute :>> modal = true;

            part fields : Row {
                part main : Column {
                    part name : TextField {
                        attribute :>> label = "Название *";
                        attribute :>> text = "GitHub";
                    }
                    part login : TextField {
                        attribute :>> label = "Логин";
                        attribute :>> text = "voluevichegor";
                    }
                    part password : PasswordField {
                        attribute :>> label = "Пароль";
                        attribute :>> text = "••••••••••••••••••••";
                        attribute :>> actions = ("Показать", "Сгенерировать");
                    }
                    part strength : Meter {
                        attribute :>> label = "Стойкость: очень высокая (≈ 118 бит)";
                        attribute :>> value = 95;
                    }
                    part reuse : Label {
                        attribute :>> text = "Этот пароль не используется в других записях";
                        attribute :>> style = TextStyle::muted;
                    }
                    part url : TextField {
                        attribute :>> label = "URL";
                        attribute :>> text = "https://github.com/login";
                    }
                }
                part extra : Column {
                    part folder : Select {
                        attribute :>> label = "Папка";
                        attribute :>> text = "Учёба";
                    }
                    part tags : TextField {
                        attribute :>> label = "Теги (через запятую)";
                        attribute :>> text = "git, учёба";
                    }
                    part totp : TextField {
                        attribute :>> label = "Секрет 2FA (Base32 или ссылка otpauth://)";
                        attribute :>> text = "JBSW Y3DP EHPK 3PXP";
                    }
                    part notes : TextArea {
                        attribute :>> label = "Заметки";
                        attribute :>> text = "Резервные коды 2FA — в записи «GitHub: коды восстановления»";
                    }
                    part favorite : Checkbox {
                        attribute :>> text = "Избранное";
                        attribute :>> checked = true;
                    }
                }
            }
            part buttons : Row {
                part required : Label {
                    attribute :>> text = "* — обязательное поле";
                    attribute :>> style = TextStyle::muted;
                    attribute :>> weight = 3;
                }
                part cancel : Button {
                    attribute :>> text = "Отмена";
                }
                part save : Button {
                    attribute :>> text = "Сохранить";
                    attribute :>> primary = true;
                }
            }
        }
    }

    /** Окно 4. Генератор паролей и парольных фраз (REQ-FUNC-011, REQ-FUNC-012). */
    package GeneratorMockup {
        private import UiKit::*;

        part generatorScreen : Screen {
            attribute :>> title = "Генератор паролей — KeyNest";
            attribute :>> width = 900;
            attribute :>> modal = true;

            part kind : Select {
                attribute :>> label = "Тип";
                attribute :>> text = "Случайный пароль";
            }
            part length : Slider {
                attribute :>> label = "Длина: 20 символов";
                attribute :>> min = 8;
                attribute :>> max = 64;
                attribute :>> value = 20;
            }
            part charsets : Row {
                part upper : Checkbox {
                    attribute :>> text = "A–Z";
                    attribute :>> checked = true;
                }
                part lower : Checkbox {
                    attribute :>> text = "a–z";
                    attribute :>> checked = true;
                }
                part digits : Checkbox {
                    attribute :>> text = "0–9";
                    attribute :>> checked = true;
                }
                part symbols : Checkbox {
                    attribute :>> text = "!@#$%^&*";
                    attribute :>> checked = true;
                }
                part similar : Checkbox {
                    attribute :>> text = "Без похожих (l, 1, O, 0)";
                    attribute :>> weight = 2;
                }
            }
            part result : PasswordField {
                attribute :>> label = "Результат";
                attribute :>> text = "q7#Vt9mL!x2RkP4w@zJe";
                attribute :>> actions = ("Обновить", "Копировать");
            }
            part strength : Meter {
                attribute :>> label = "Стойкость: очень высокая (≈ 129 бит)";
                attribute :>> value = 100;
            }
            part phraseHint : Label {
                attribute :>> text = "Тип «Парольная фраза»: 4–10 слов из словаря, разделитель, заглавные буквы, цифра";
                attribute :>> style = TextStyle::muted;
            }
            part buttons : Row {
                part gap : Column {
                    attribute :>> weight = 2;
                }
                part close : Button {
                    attribute :>> text = "Закрыть";
                }
                part useButton : Button {
                    attribute :>> text = "Подставить в запись";
                    attribute :>> primary = true;
                }
            }
        }
    }

    /** Окно 5. Аудит безопасности (REQ-FUNC-012, REQ-FUNC-013, REQ-INT-001). */
    package AuditMockup {
        private import UiKit::*;

        part auditScreen : Screen {
            attribute :>> title = "Аудит безопасности — Личное.knv — KeyNest";

            part menu : MenuBar {
                attribute :>> items = ("Файл", "Правка", "Вид", "Сервис", "Справка");
            }
            part stats : Row {
                part score : StatCard {
                    attribute :>> label = "Оценка хранилища";
                    attribute :>> text = "72 / 100";
                }
                part weak : StatCard {
                    attribute :>> label = "Слабые";
                    attribute :>> text = "3";
                }
                part reused : StatCard {
                    attribute :>> label = "Повторяющиеся";
                    attribute :>> text = "4 в 2 группах";
                }
                part pwned : StatCard {
                    attribute :>> label = "Найдены в утечках";
                    attribute :>> text = "1";
                }
                part old : StatCard {
                    attribute :>> label = "Не менялись > 1 года";
                    attribute :>> text = "6";
                }
            }
            part problems : Panel {
                attribute :>> text = "Проблемы (сначала самые опасные)";
                part table : Table {
                    attribute :>> columns = ("Запись", "Проблема", "Подробности", "Действие");
                    part r1 : TableRow {
                        attribute :>> cells = ("Zoom", "Найден в утечках", "Встречается в 1 204 утечках", "Сменить пароль");
                    }
                    part r2 : TableRow {
                        attribute :>> cells = ("Stepik", "Повторяется", "Совпадает с «Zoom»", "Сменить пароль");
                    }
                    part r3 : TableRow {
                        attribute :>> cells = ("Wi-Fi общежития", "Слабый", "8 символов, только цифры (≈ 27 бит)", "Сменить пароль");
                    }
                    part r4 : TableRow {
                        attribute :>> cells = ("Overleaf", "Старый", "Не менялся 389 дней", "Сменить или скрыть");
                    }
                    part r5 : TableRow {
                        attribute :>> cells = ("ИИС «БГУИР»", "Нет 2FA", "Сервис поддерживает 2FA, секрет не задан", "Добавить 2FA");
                    }
                }
            }
            part bottom : Row {
                part source : Label {
                    attribute :>> text = "Утечки: Have I Been Pwned, передаются только 5 первых символов хеша SHA-1 · Проверено 13.11.2026 11:30";
                    attribute :>> style = TextStyle::muted;
                    attribute :>> weight = 4;
                }
                part check : Button {
                    attribute :>> text = "Проверить утечки сейчас";
                }
            }
        }
    }

    /** Окно 6. Настройки (REQ-FUNC-016 … REQ-FUNC-020, REQ-SEC-004, REQ-SEC-005, REQ-INT-001). */
    package SettingsMockup {
        private import UiKit::*;

        part settingsScreen : Screen {
            attribute :>> title = "Настройки — KeyNest";
            attribute :>> width = 1100;
            attribute :>> modal = true;

            part columns : Row {
                part left : Column {
                    part security : Panel {
                        attribute :>> text = "Безопасность";
                        part autolock : SpinBox {
                            attribute :>> label = "Автоблокировка при бездействии, мин (1–30)";
                            attribute :>> text = "5";
                        }
                        part osLock : Checkbox {
                            attribute :>> text = "Блокировать при блокировке экрана и сне";
                            attribute :>> checked = true;
                        }
                        part clipboard : SpinBox {
                            attribute :>> label = "Очищать буфер обмена через, с (10–120)";
                            attribute :>> text = "30";
                        }
                        part pwned : Checkbox {
                            attribute :>> text = "Проверять пароли по базе утечек HIBP";
                            attribute :>> checked = true;
                        }
                        part changeMaster : Button {
                            attribute :>> text = "Сменить мастер-пароль…";
                        }
                    }
                }
                part right : Column {
                    part backup : Panel {
                        attribute :>> text = "Резервные копии";
                        part enabled : Checkbox {
                            attribute :>> text = "Создавать копию при каждом сохранении";
                            attribute :>> checked = true;
                        }
                        part keep : SpinBox {
                            attribute :>> label = "Хранить последних копий (1–50)";
                            attribute :>> text = "10";
                        }
                        part folder : TextField {
                            attribute :>> label = "Папка резервных копий";
                            attribute :>> text = "C:\\Users\\student\\Documents\\KeyNest\\backup";
                            attribute :>> actions = ("Обзор…");
                        }
                    }
                    part transfer : Panel {
                        attribute :>> text = "Перенос данных";
                        part buttons : Row {
                            part importButton : Button {
                                attribute :>> text = "Импорт из CSV…";
                            }
                            part exportButton : Button {
                                attribute :>> text = "Экспорт…";
                            }
                        }
                        part note : Label {
                            attribute :>> text = "Экспорт в CSV не шифруется — потребуется мастер-пароль";
                            attribute :>> style = TextStyle::muted;
                        }
                    }
                }
            }
            part buttons : Row {
                part language : Select {
                    attribute :>> text = "Язык: русский";
                    attribute :>> weight = 2;
                }
                part gap : Column {
                    attribute :>> weight = 2;
                }
                part cancel : Button {
                    attribute :>> text = "Отмена";
                }
                part save : Button {
                    attribute :>> text = "Сохранить";
                    attribute :>> primary = true;
                }
            }
        }
    }
}
