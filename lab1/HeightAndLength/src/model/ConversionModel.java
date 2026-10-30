package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Optional;

/**
 * АКТИВНАЯ МОДЕЛЬ.
 *
 * Хранит состояние приложения (последние данные и результат) и сама оповещает
 * подписанные представления об изменениях через механизм PropertyChange
 * (паттерн «Наблюдатель»). Контроллер лишь передаёт модели команды и не
 * занимается обновлением представления.
 */
public class ConversionModel {

    /** Имя свойства, о смене которого модель оповещает подписчиков. */
    public static final String PROPERTY_CONVERSION = "conversion";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final InputStorage storage;

    private Conversion conversion;

    public ConversionModel(InputStorage storage) {
        this.storage = storage;
        // Восстанавливаем данные, введённые при прошлом запуске.
        this.conversion = storage.load().map(Conversion::of).orElse(null);
    }

    /**
     * Принимает новые исходные данные: пересчитывает результат, запоминает данные
     * и оповещает всех подписчиков.
     */
    public void setInput(ConversionInput input) {
        Conversion old = this.conversion;
        this.conversion = Conversion.of(input);
        storage.save(input);
        support.firePropertyChange(PROPERTY_CONVERSION, old, conversion);
    }

    /** Последняя выполненная конвертация (пусто, если данные ещё ни разу не вводились). */
    public Optional<Conversion> getConversion() {
        return Optional.ofNullable(conversion);
    }

    /** Последние введённые данные — ими заполняется окно ввода. */
    public Optional<ConversionInput> getLastInput() {
        return getConversion().map(Conversion::input);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
