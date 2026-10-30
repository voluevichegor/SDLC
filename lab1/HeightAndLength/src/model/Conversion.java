package model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Результат конвертации: исходные данные и вычисленные значения.
 *
 * @param input       исходные данные
 * @param result      длина в целевых единицах
 * @param inAllUnits  та же длина во всех поддерживаемых единицах
 */
public record Conversion(ConversionInput input, double result, Map<LengthUnit, Double> inAllUnits) {

    public Conversion {
        inAllUnits = Map.copyOf(inAllUnits);
    }

    /** Переводит длину {@code value} из единиц {@code from} в единицы {@code to}. */
    public static double convert(double value, LengthUnit from, LengthUnit to) {
        if (from == to) {
            return value;
        }
        double meters = value * from.getMeters();
        return meters / to.getMeters();
    }

    /** Выполняет конвертацию для заданных исходных данных. */
    public static Conversion of(ConversionInput input) {
        Map<LengthUnit, Double> all = new EnumMap<>(LengthUnit.class);
        for (LengthUnit unit : LengthUnit.values()) {
            all.put(unit, convert(input.value(), input.from(), unit));
        }
        return new Conversion(input, all.get(input.to()), all);
    }
}
