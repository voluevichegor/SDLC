package view;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Форматирование чисел для показа пользователю (русская локаль, 7 значащих цифр). */
public final class NumberFormats {

    private static final Locale RU = Locale.forLanguageTag("ru-RU");
    private static final MathContext SIGNIFICANT = new MathContext(7);

    private NumberFormats() {
    }

    public static String format(double value) {
        if (value == 0) {
            return "0";
        }
        double abs = Math.abs(value);
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(RU);
        if (abs >= 1e-4 && abs < 1e12) {
            BigDecimal rounded = new BigDecimal(value).round(SIGNIFICANT);
            DecimalFormat plain = new DecimalFormat("#,##0.##########", symbols);
            return plain.format(rounded);
        }
        DecimalFormat scientific = new DecimalFormat("0.######E0", symbols);
        return scientific.format(value).replace("E", " × 10^");
    }

    /** Значение для поля ввода: без разделителей разрядов, чтобы его можно было сразу редактировать. */
    public static String forInputField(double value) {
        String plain = new BigDecimal(value).round(new MathContext(15)).stripTrailingZeros().toPlainString();
        return plain.replace('.', ',');
    }
}
