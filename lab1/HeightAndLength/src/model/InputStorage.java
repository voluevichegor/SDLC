package model;

import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * Хранит последние введённые данные между запусками программы
 * (через java.util.prefs: в Windows — реестр, в Linux/macOS — файл в домашнем каталоге).
 */
public class InputStorage {

    private static final String KEY_VALUE = "value";
    private static final String KEY_FROM = "fromUnit";
    private static final String KEY_TO = "toUnit";

    private final Preferences preferences;

    public InputStorage() {
        this(Preferences.userNodeForPackage(InputStorage.class).node("heightAndLength"));
    }

    /** Конструктор для подстановки другого узла настроек (например, при отладке). */
    public InputStorage(Preferences preferences) {
        this.preferences = preferences;
    }

    public void save(ConversionInput input) {
        preferences.putDouble(KEY_VALUE, input.value());
        preferences.put(KEY_FROM, input.from().name());
        preferences.put(KEY_TO, input.to().name());
    }

    /** Возвращает сохранённые данные или пустой Optional, если сохранения нет или оно повреждено. */
    public Optional<ConversionInput> load() {
        double value = preferences.getDouble(KEY_VALUE, Double.NaN);
        String from = preferences.get(KEY_FROM, null);
        String to = preferences.get(KEY_TO, null);
        if (Double.isNaN(value) || value <= 0 || from == null || to == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new ConversionInput(value, LengthUnit.valueOf(from), LengthUnit.valueOf(to)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
