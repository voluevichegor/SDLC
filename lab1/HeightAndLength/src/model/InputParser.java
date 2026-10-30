package model;

/**
 * Разбор и проверка введённых пользователем данных.
 * Правила корректности — часть бизнес-логики, поэтому класс находится в модели.
 */
public final class InputParser {

    /** Максимальная длина, которую принимает программа (в исходных единицах). */
    public static final double MAX_VALUE = 1e12;

    private InputParser() {
    }

    /**
     * Превращает текст из поля ввода и выбранные единицы в {@link ConversionInput}.
     *
     * @throws InvalidInputException если данные некорректны; сообщение готово для показа пользователю
     */
    public static ConversionInput parse(String rawValue, LengthUnit from, LengthUnit to)
            throws InvalidInputException {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidInputException("Введите длину.");
        }
        if (from == null || to == null) {
            throw new InvalidInputException("Выберите исходные и целевые единицы измерения.");
        }

        // Пользователь может ввести дробную часть как через точку, так и через запятую;
        // пробелы внутри числа («1 500») тоже допускаем.
        String normalized = rawValue.trim()
                .replace(' ', ' ')
                .replace(" ", "")
                .replace(',', '.');

        double value;
        try {
            value = Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(
                    "«" + rawValue.trim() + "» не является числом.\nПример корректного ввода: 1,75");
        }

        // Double.parseDouble принимает «NaN», «Infinity» и суффиксы вида «5d» — отсекаем их.
        if (!normalized.matches("[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE][+-]?\\d+)?")) {
            throw new InvalidInputException(
                    "«" + rawValue.trim() + "» не является числом.\nПример корректного ввода: 1,75");
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new InvalidInputException("Слишком большое число.");
        }
        if (value <= 0) {
            throw new InvalidInputException("Длина должна быть больше нуля.");
        }
        if (value > MAX_VALUE) {
            throw new InvalidInputException("Длина не должна превышать 1 000 000 000 000.");
        }
        return new ConversionInput(value, from, to);
    }
}
