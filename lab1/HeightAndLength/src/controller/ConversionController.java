package controller;

import model.ConversionInput;
import model.ConversionModel;
import model.InputParser;
import model.InvalidInputException;
import view.InputDialog;
import view.MainFrame;

/**
 * Контроллер: принимает действия пользователя и передаёт команды модели.
 *
 * Обновлять главное окно контроллеру не нужно — при активной модели это делает
 * сама модель, оповещая подписанное на неё представление.
 */
public class ConversionController {

    private final ConversionModel model;
    private final MainFrame view;

    public ConversionController(ConversionModel model, MainFrame view) {
        this.model = model;
        this.view = view;
        view.setInputAction(e -> openInputDialog());
    }

    /** Кнопка «Ввести данные»: открыть окно ввода, заполненное последними данными. */
    private void openInputDialog() {
        InputDialog dialog = new InputDialog(view);
        model.getLastInput().ifPresentOrElse(dialog::fill, dialog::fillDefaults);
        dialog.setConvertAction(e -> submit(dialog));
        dialog.open();
    }

    /** Кнопка «Конвертировать»: проверить данные и передать их модели. */
    private void submit(InputDialog dialog) {
        try {
            ConversionInput input = InputParser.parse(
                    dialog.getValueText(), dialog.getFromUnit(), dialog.getToUnit());
            model.setInput(input);
            dialog.dispose();
        } catch (InvalidInputException ex) {
            // Окно ввода остаётся открытым, чтобы пользователь исправил данные.
            view.showError(dialog, ex.getMessage());
            dialog.focusValueField();
        }
    }
}
