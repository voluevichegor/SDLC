package model;

import java.util.Objects;

/**
 * Исходные данные одной конвертации: длина и две единицы измерения.
 * Объект неизменяемый; корректность значения проверяет {@link InputParser}.
 *
 * @param value длина в исходных единицах (положительное конечное число)
 * @param from  единицы, в которых пользователь ввёл длину
 * @param to    единицы, в которые нужно перевести длину
 */
public record ConversionInput(double value, LengthUnit from, LengthUnit to) {

    public ConversionInput {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
    }
}
