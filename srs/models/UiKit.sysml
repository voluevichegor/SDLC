/**
 * UiKit — библиотека элементов пользовательского интерфейса для
 * прототипирования окон (мокапов) настольного приложения KeyNest на SysML v2.
 *
 * Окно описывается как дерево частей (part): контейнеры задают компоновку,
 * листовые элементы — поля, кнопки, списки, таблицы и индикаторы.
 * Элементы соответствуют стандартным виджетам Qt 6 Widgets
 * (QMainWindow, QDialog, QLineEdit, QListView, QTableView, QProgressBar и т. д.).
 */
package UiKit {
    private import ScalarValues::*;

    enum def Orientation {
        enum vertical;
        enum horizontal;
    }

    enum def TextStyle {
        enum heading;
        enum normal;
        enum muted;
        enum warning;
    }

    /** Базовый элемент интерфейса. */
    abstract part def Widget {
        /** Отображаемый текст: надпись, заголовок или пример значения. */
        attribute text : String [0..1];
        /** Относительная ширина элемента в горизонтальном контейнере. */
        attribute weight : Integer = 1;
    }

    /** Контейнер: размещает вложенные элементы вертикально или горизонтально. */
    abstract part def Container :> Widget {
        attribute orientation : Orientation = Orientation::vertical;
        part children : Widget [0..*];
    }

    /** Окно приложения (QMainWindow) или модальный диалог (QDialog). */
    part def Screen :> Container {
        /** Заголовок окна. */
        attribute title : String;
        /** Ширина окна в пикселях (по умолчанию 1280). */
        attribute width : Integer = 1280;
        attribute modal : Boolean = false;
    }

    /** Строка: горизонтальный контейнер без рамки. */
    part def Row :> Container {
        attribute :>> orientation = Orientation::horizontal;
    }

    /** Колонка: вертикальный контейнер без рамки. */
    part def Column :> Container;

    /** Панель (группа, QGroupBox): контейнер с рамкой и необязательным заголовком text. */
    part def Panel :> Container;

    /** Строка меню окна (QMenuBar). */
    part def MenuBar :> Widget {
        attribute items : String [0..*];
    }

    /** Строка состояния окна (QStatusBar). */
    part def StatusBar :> Widget;

    /** Список с выделенным пунктом (QListView): навигация по разделам и папкам. */
    part def ListView :> Widget {
        attribute items : String [0..*];
        attribute active : String [0..1];
    }

    part def Label :> Widget {
        attribute style : TextStyle = TextStyle::normal;
    }

    part def Link :> Widget;

    /**
     * Поле ввода (QLineEdit): label — подпись над полем, text — пример значения,
     * actions — кнопки внутри поля справа (показать, копировать, сгенерировать).
     */
    part def TextField :> Widget {
        attribute label : String [0..1];
        attribute actions : String [0..*];
    }

    part def PasswordField :> TextField;

    /** Многострочное поле (QPlainTextEdit). */
    part def TextArea :> TextField;

    /** Выпадающий список (QComboBox). */
    part def Select :> TextField;

    /** Числовое поле со стрелками (QSpinBox). */
    part def SpinBox :> TextField;

    part def Checkbox :> Widget {
        attribute checked : Boolean = false;
    }

    part def Button :> Widget {
        attribute primary : Boolean = false;
    }

    /** Ползунок (QSlider): label — подпись, value — положение в диапазоне min..max. */
    part def Slider :> Widget {
        attribute label : String [0..1];
        attribute min : Integer = 0;
        attribute max : Integer = 100;
        attribute value : Integer;
    }

    /** Индикатор (QProgressBar): стойкость пароля, оставшееся время кода TOTP. */
    part def Meter :> Widget {
        attribute label : String [0..1];
        /** Заполнение в процентах, 0..100. */
        attribute value : Integer;
    }

    /** Карточка показателя: label — название, text — значение. */
    part def StatCard :> Widget {
        attribute label : String;
    }

    part def TableRow :> Widget {
        attribute cells : String [0..*];
        attribute selected : Boolean = false;
    }

    /** Таблица (QTableView). */
    part def Table :> Widget {
        attribute columns : String [1..*];
        part rows : TableRow [0..*];
    }
}
