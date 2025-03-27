package common

import org.kohsuke.args4j.Option

import java.nio.file.Files
import java.nio.file.Path

/**
 * Abstract class for scripts that read input and write output (either from a file or the clipboard)
 *
 * @author craig.moore@agfa.com - aptng
 */
abstract class IOScript {

    private final ClipboardUtils clipboardUtils
    IOScript() {
        clipboardUtils = new ClipboardUtils()
    }

    static class Options {
        @Option(name = '--help', aliases = ['-h'], usage = "Show this help menu", required = false)
        boolean help = false

        @Option(name = '--inFile', aliases = ['-f'], usage = 'The file to read in and add line numbers to', required = false)
        String inFilePath

        @Option(name = '--update', aliases = ['-u'], usage = 'Inline update the file to read in (only works with the --inFile option)')
        boolean updateInFile = false

        @Option(name = '--outFile', aliases = ['-o'],
                usage = 'The file to write the output text to (by default the output is saved to the clipboard)',
                required = false)
        String outFilePath

        @Option(name = '--debug', aliases = ['-d'], usage = "Print debug output", required = false)
        boolean debug = false

        @Option(name = '--clipboard', aliases = ['-cp'], usage = 'Read the input from the clipboard', required = false)
        boolean useClipboard = false
    }

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
            inputLines = clipboardUtils.getClipboardContents()
        } else if (System.console() == null || System.in.available() > 0) {
            inputLines = System.in.readLines()
        } else {
            throw new IllegalStateException('Don\'t know where to read from (no interactive console found), (use `--clipboard` flag?)')
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
            clipboardUtils.setClipboardContents(outputString)
            ColorLogger.info "Saved output to clipboard"
        } else {
            println outputString
        }
    }
}
