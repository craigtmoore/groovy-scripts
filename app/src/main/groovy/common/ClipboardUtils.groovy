package common

import groovy.util.logging.Slf4j

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

/**
 * Utility class for clipboard operations
 *
 * @author Craig Moore
 */
@Slf4j
class ClipboardUtils {

    List<String> getClipboardContents() {
        List<String> lines = new ArrayList<>()
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        Transferable contents = clipboard.getContents(null)
        boolean hasStringText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)
        if (hasStringText) {
            try {
                String clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor)
                if (clipboardText) {
                    lines = Arrays.asList(clipboardText.split("\\r?\\n")) // Split by newline characters
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                throw new ClipboardException("Failed to read clipboard contents", ex)
            }
        }
        return lines
    }

    void setClipboardContents(String text) {
        StringSelection stringSelection = new StringSelection(text)
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        clipboard.setContents(stringSelection, null)
    }

    static class ClipboardException extends RuntimeException {
        ClipboardException(String message, Exception e) {
            super(message, e)
        }
    }
}
