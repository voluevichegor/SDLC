package view;

import model.Conversion;
import model.ConversionInput;
import model.ConversionModel;
import model.LengthUnit;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Главное окно (представление).
 *
 * В схеме MVC с активной моделью представление подписывается на модель напрямую
 * и перерисовывается само, когда модель сообщает об изменении.
 */
public class MainFrame extends JFrame implements PropertyChangeListener {

    private static final String EMPTY = "—";

    private final JLabel valueLabel = new JLabel(EMPTY);
    private final JLabel fromLabel = new JLabel(EMPTY);
    private final JLabel toLabel = new JLabel(EMPTY);
    private final JLabel resultLabel = new JLabel(EMPTY);
    private final JLabel hintLabel = new JLabel(" ");
    private final JButton inputButton = new JButton("Ввести данные");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Единица", "1 единица в метрах", "Значение"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public MainFrame(ConversionModel model) {
        setTitle("Высота и длина");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        buildUi();
        setSize(720, 600);
        setMinimumSize(new Dimension(620, 520));
        setLocationRelativeTo(null);

        // Подписка представления на модель — ключевой элемент активной модели.
        model.addPropertyChangeListener(this);
        render(model.getConversion().orElse(null));
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Высота и длина", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitle = new JLabel(
                "Перевод длины между обычными и не очень обычными единицами измерения",
                SwingConstants.CENTER);
        JPanel header = new JPanel(new BorderLayout(4, 6));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        JPanel values = new JPanel(new GridBagLayout());
        values.setBorder(BorderFactory.createTitledBorder("Последние введённые данные и результат"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        addRow(values, c, 0, "Длина:", valueLabel);
        addRow(values, c, 1, "Исходные единицы:", fromLabel);
        addRow(values, c, 2, "Целевые единицы:", toLabel);
        addRow(values, c, 3, "Результат:", resultLabel);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        values.add(hintLabel, c);

        Font valueFont = valueLabel.getFont().deriveFont(Font.BOLD, 14f);
        valueLabel.setFont(valueFont);
        fromLabel.setFont(valueFont);
        toLabel.setFont(valueFont);
        resultLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 22f));
        resultLabel.setForeground(new Color(0x1F5FAF));
        hintLabel.setForeground(Color.GRAY);

        table.setRowHeight(22);
        table.setFocusable(false);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(right);
        table.getColumnModel().getColumn(2).setCellRenderer(right);
        table.getColumnModel().getColumn(0).setPreferredWidth(220);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Та же длина во всех единицах"));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(values, BorderLayout.NORTH);
        center.add(tableScroll, BorderLayout.CENTER);

        inputButton.setPreferredSize(new Dimension(200, 36));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(inputButton);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private static void addRow(JPanel panel, GridBagConstraints c, int row, String label, JLabel value) {
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(value, c);
    }

    /** Реакция на оповещение от модели. */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (ConversionModel.PROPERTY_CONVERSION.equals(event.getPropertyName())) {
            render((Conversion) event.getNewValue());
        }
    }

    private void render(Conversion conversion) {
        tableModel.setRowCount(0);
        if (conversion == null) {
            valueLabel.setText(EMPTY);
            fromLabel.setText(EMPTY);
            toLabel.setText(EMPTY);
            resultLabel.setText(EMPTY);
            hintLabel.setText("Нажмите «Ввести данные», чтобы выполнить первый перевод.");
            for (LengthUnit unit : LengthUnit.values()) {
                tableModel.addRow(new Object[]{unit, NumberFormats.format(unit.getMeters()), EMPTY});
            }
            return;
        }
        ConversionInput input = conversion.input();
        valueLabel.setText(NumberFormats.format(input.value()));
        fromLabel.setText(input.from().getDisplayName());
        toLabel.setText(input.to().getDisplayName());
        resultLabel.setText(NumberFormats.format(conversion.result()));
        LengthUnit to = input.to();
        hintLabel.setText("Справка: " + to.getDisplayName().toLowerCase() + " = "
                + NumberFormats.format(to.getMeters()) + " м (" + to.getNote() + ")");
        for (LengthUnit unit : LengthUnit.values()) {
            tableModel.addRow(new Object[]{
                    unit,
                    NumberFormats.format(unit.getMeters()),
                    NumberFormats.format(conversion.inAllUnits().get(unit))
            });
        }
    }

    public void setInputAction(ActionListener listener) {
        inputButton.addActionListener(listener);
    }

    /** Сообщение об ошибке ввода поверх указанного окна. */
    public void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
    }
}
