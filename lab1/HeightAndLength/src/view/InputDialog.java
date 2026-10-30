package view;

import model.ConversionInput;
import model.LengthUnit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Дополнительное окно ввода данных: длина, исходные и целевые единицы.
 *
 * Окно ничего не проверяет и не вычисляет — по нажатию «Конвертировать» оно вызывает
 * действие, которое назначил контроллер. Закрывает окно тоже контроллер, когда данные корректны.
 */
public class InputDialog extends JDialog {

    private final JTextField valueField = new JTextField(14);
    private final JComboBox<LengthUnit> fromBox = new JComboBox<>(LengthUnit.values());
    private final JComboBox<LengthUnit> toBox = new JComboBox<>(LengthUnit.values());
    private final JButton convertButton = new JButton("Конвертировать");
    private final JButton cancelButton = new JButton("Отмена");
    private final JButton swapButton = new JButton("⇅ Поменять местами");

    public InputDialog(Frame owner) {
        super(owner, "Ввод данных", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUi();
        pack();
        setMinimumSize(getSize());
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void buildUi() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(18, 20, 6, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        valueField.setToolTipText("Положительное число; дробную часть можно отделять запятой или точкой");
        fromBox.setToolTipText("Единицы, в которых введена длина");
        toBox.setToolTipText("Единицы, в которые нужно перевести длину");

        addRow(form, c, 0, "Длина:", valueField);
        addRow(form, c, 1, "Из единиц:", fromBox);
        addRow(form, c, 2, "В единицы:", toBox);

        c.gridx = 1;
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        form.add(swapButton, c);

        swapButton.addActionListener(e -> {
            Object from = fromBox.getSelectedItem();
            fromBox.setSelectedItem(toBox.getSelectedItem());
            toBox.setSelectedItem(from);
        });
        cancelButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttons.add(convertButton);
        buttons.add(cancelButton);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(convertButton);
        // Esc закрывает окно так же, как «Отмена».
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private static void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0;
        c.gridy = row;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        panel.add(field, c);
    }

    /** Заполняет поля последними введёнными данными. */
    public void fill(ConversionInput input) {
        valueField.setText(NumberFormats.forInputField(input.value()));
        fromBox.setSelectedItem(input.from());
        toBox.setSelectedItem(input.to());
    }

    /** Значения по умолчанию для самого первого запуска. */
    public void fillDefaults() {
        valueField.setText("");
        fromBox.setSelectedItem(LengthUnit.METER);
        toBox.setSelectedItem(LengthUnit.GIRAFFE_NECK);
    }

    public void setConvertAction(ActionListener listener) {
        convertButton.addActionListener(listener);
    }

    public String getValueText() {
        return valueField.getText();
    }

    public LengthUnit getFromUnit() {
        return (LengthUnit) fromBox.getSelectedItem();
    }

    public LengthUnit getToUnit() {
        return (LengthUnit) toBox.getSelectedItem();
    }

    /** Выделяет поле длины, чтобы после сообщения об ошибке пользователь сразу мог его исправить. */
    public void focusValueField() {
        valueField.requestFocusInWindow();
        valueField.selectAll();
    }

    /** Открывает окно с выделенным полем длины (метод блокирует поток до закрытия окна). */
    public void open() {
        SwingUtilities.invokeLater(this::focusValueField);
        setVisible(true);
    }
}
