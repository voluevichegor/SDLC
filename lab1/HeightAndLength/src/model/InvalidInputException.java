package model;

/** Ошибка во введённых пользователем данных. Текст сообщения показывается пользователю. */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }
}
