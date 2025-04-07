package common

import java.nio.file.Files
import java.nio.file.Path

/**
 * Default implementation of the IOScript interface.
 *
 * @author craig.moore@agfa.com - aptng
 */
class IOScriptImpl implements IOScript {

    private final ClipboardAccessor clipboardAccessor
    IOScriptImpl(ClipboardAccessor clipboardAccessor) { this.clipboardAccessor = clipboardAccessor }
    IOScriptImpl() { this(new ClipboardAccessorImpl()) }

    List<String> readInput(log, Options options) {
        def inputLines
        if (options.inFilePath) {
            def inFile = Path.of(options.inFilePath)
            if (!Files.exists(inFile)) {
                throw new FileNotFoundException("No file found at path '$options.inFilePath'")
            }
            log.info "Reading text from file '$options.inFilePath'"
            inputLines = Files.readAllLines(inFile)
            if (options.updateInFile) {
                log.debug 'Observed --update flag, so setting option.outFilePath to option.inFilePath'
                options.outFilePath = options.inFilePath
            }
        } else if (options.useClipboard) {
            if (options.updateInFile) {
                log.warn("Ignoring --update(-u) option as there was no --inFile provided")
            }
            log.info "Reading text from the clipboard"
            inputLines = clipboardAccessor.getClipboardContents()
        } else if (System.console() == null || System.in.available() > 0) {
            // Console is unavailable while piping text through standard input
            log.info "Reading text from standard input"
            inputLines = System.in.readLines()
        } else {
            throw new IllegalStateException('Don\'t know where to read from (no interactive console found), (use `--clipboard` flag?)')
        }
        if (!inputLines) {
            throw new IllegalStateException('No input provided')
        }
        return inputLines
    }

    void writeOutput(Options options, String outputString) {
        if (options.outFilePath) {
            if (options.debug) println outputString
            def outFile = Path.of(options.outFilePath)
            Files.writeString(outFile, outputString)
            ColorLogger.messageBuilder().green("Saved output to '").yellow(options.outFilePath).green("'").log()
        } else if (options.useClipboard) {
            if (options.debug) println outputString
            clipboardAccessor.setClipboardContents(outputString)
            ColorLogger.info "Saved output to clipboard"
        } else {
            println outputString
        }
    }
}
