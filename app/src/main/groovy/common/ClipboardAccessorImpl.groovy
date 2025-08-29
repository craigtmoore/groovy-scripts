package common

import common.exceptions.ClipboardException
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
class ClipboardAccessorImpl implements ClipboardAccessor {

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

    /**
     * Note: this doesn't work when pasting into Microsoft Powerpoint, only works for Word or JIRA
     * @param htmlText text that has HTML hyperlinks or other html code embedded
     */
    void setHtmlClipboardContents(String htmlText) {
        // Build HTML clipboard format with required markers
        String fragment = "<!--StartFragment-->${htmlText}<!--EndFragment-->"
        String html = "<html><body>${fragment}</body></html>"
        String header = "Version:0.9\r\n"
        int startHTML = header.length()
        int startFragment = startHTML + html.indexOf("<!--StartFragment-->")
        int endFragment = startHTML + html.indexOf("<!--EndFragment-->") + "<!--EndFragment-->".length()
        int endHTML = startHTML + html.length()
        header += "StartHTML:${String.format('%08d', startHTML)}\r\n"
        header += "EndHTML:${String.format('%08d', endHTML)}\r\n"
        header += "StartFragment:${String.format('%08d', startFragment)}\r\n"
        header += "EndFragment:${String.format('%08d', endFragment)}\r\n"
        String clipboardHtml = header + html
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        Transferable transferable = new Transferable() {
            public static final DataFlavor HTML_FLAVOR = new DataFlavor(
                    "text/html;class=java.lang.String", "HTML Format"
            );
            @Override
            DataFlavor[] getTransferDataFlavors() {
                return [
                        DataFlavor.selectionHtmlFlavor,
                        DataFlavor.fragmentHtmlFlavor,
                        DataFlavor.allHtmlFlavor,
                        HTML_FLAVOR,
                        DataFlavor.stringFlavor
                ] as DataFlavor[]
            }

            @Override
            boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor == DataFlavor.selectionHtmlFlavor ||
                        flavor == DataFlavor.fragmentHtmlFlavor ||
                        flavor == DataFlavor.allHtmlFlavor ||
                        flavor == HTML_FLAVOR ||
                        flavor == DataFlavor.stringFlavor
            }

            @Override
            Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (flavor == DataFlavor.stringFlavor) {
                    return htmlText
                }
                if (isDataFlavorSupported(flavor)) {
                    return clipboardHtml
                }
            }
        }
        clipboard.setContents(transferable, null)
    }
}
