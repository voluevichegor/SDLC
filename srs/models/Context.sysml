/**
 * Контекст системы KeyNest и карта переходов между окнами.
 * PNG-изображения строятся из этой модели: srs/tools/sysml2png.py.
 */
package KeyNestContext {

    part def Person;
    part def DesktopApplication;
    part def EncryptedFile;
    part def Folder;
    part def OperatingSystem;
    part def ExternalService;

    /** Контекстная диаграмма: система, её пользователь, файлы и внешние сервисы. */
    part systemContext {
        part user : Person {
            doc /* Пользователь */
        }
        part keyNest : DesktopApplication {
            doc /* KeyNest (C++ 20, Qt 6) */
        }
        part vaultFile : EncryptedFile {
            doc /* Файл хранилища *.knv */
        }
        part backups : EncryptedFile {
            doc /* Резервные копии *.knv */
        }
        part cloudFolder : Folder {
            doc /* Облачная папка (необязательно) */
        }
        part os : OperatingSystem {
            doc /* ОС Windows / Linux */
        }
        part hibp : ExternalService {
            doc /* Have I Been Pwned: Pwned Passwords API */
        }
        part browser : DesktopApplication {
            doc /* Веб-браузер по умолчанию */
        }

        connection masterPassword connect user to keyNest {
            doc /* мастер-пароль, работа с записями */
        }
        connection storage connect keyNest to vaultFile {
            doc /* чтение и запись, данные зашифрованы */
        }
        connection backup connect keyNest to backups {
            doc /* копия при каждом сохранении */
        }
        connection sync connect vaultFile to cloudFolder {
            doc /* синхронизация облачным клиентом */
        }
        connection system connect os to keyNest {
            doc /* буфер обмена, события блокировки и сна */
        }
        connection pwned connect hibp to keyNest {
            doc /* HTTPS: 5 первых символов SHA-1 */
        }
        connection openUrl connect browser to keyNest {
            doc /* открытие URL записи */
        }
    }

    item def Unlocked;
    item def Locked;
    item def AddOrEdit;
    item def SavedOrCancelled;
    item def OpenGenerator;
    item def UsePassword;
    item def Closed;
    item def OpenAudit;
    item def FixProblem;
    item def OpenSettings;

    /** Карта окон: состояния — окна, переходы — действия пользователя и события. */
    state def ScreenFlow {
        entry; then unlock;

        state unlock {
            doc /* Разблокировка */
        }
        state mainWindow {
            doc /* Главное окно */
        }
        state entryEditor {
            doc /* Запись */
        }
        state generator {
            doc /* Генератор */
        }
        state audit {
            doc /* Аудит безопасности */
        }
        state settings {
            doc /* Настройки */
        }

        transition doUnlock first unlock accept Unlocked then mainWindow;
        transition openEditor first mainWindow accept AddOrEdit then entryEditor;
        transition closeEditor first entryEditor accept SavedOrCancelled then mainWindow;
        transition editorToGenerator first entryEditor accept OpenGenerator then generator;
        transition generatorToEditor first generator accept UsePassword then entryEditor;
        transition mainToGenerator first mainWindow accept OpenGenerator then generator;
        transition generatorToMain first generator accept Closed then mainWindow;
        transition toAudit first mainWindow accept OpenAudit then audit;
        transition auditToEditor first audit accept FixProblem then entryEditor;
        transition auditToMain first audit accept Closed then mainWindow;
        transition toSettings first mainWindow accept OpenSettings then settings;
        transition settingsToMain first settings accept SavedOrCancelled then mainWindow;
        transition lock first mainWindow accept Locked then unlock;
    }
}
