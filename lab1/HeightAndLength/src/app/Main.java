package app;

import controller.ConversionController;
import model.ConversionModel;
import model.InputStorage;
import view.MainFrame;

import javax.swing.*;

/** Точка входа: собирает модель, представление и контроллер. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Если системное оформление недоступно, остаётся стандартное оформление Swing.
            }
            ConversionModel model = new ConversionModel(new InputStorage());
            MainFrame view = new MainFrame(model);
            new ConversionController(model, view);
            view.setVisible(true);
        });
    }
}
