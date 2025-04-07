package common.exceptions

/**
 * An exception that is thrown when there is an error with the clipboard.
 * 
 * @author craig.moore@agfa.com - aptng
 */
class ClipboardException extends RuntimeException {
    ClipboardException(String message, Exception e) {
        super(message, e)
    }
}
