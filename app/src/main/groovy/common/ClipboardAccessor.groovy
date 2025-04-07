package common

/**
 * An interface for classes that need to access the clipboard.
 *
 * @author craig.moore@agfa.com - aptng
 */
interface ClipboardAccessor {

    /**
     * Reads the contents of the clipboard and returns it as a list of strings.
     *
     * @return A list of strings representing the clipboard contents.
     */
    List<String> getClipboardContents()

    /**
     * Sets the contents of the clipboard to the specified text.
     *
     * @param text The text to set in the clipboard.
     */
    void setClipboardContents(String text)



}