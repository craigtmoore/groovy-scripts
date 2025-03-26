package file

import common.ClipboardUtils
import common.ColorLogger
import groovy.util.logging.Slf4j
import org.kohsuke.args4j.Option

import java.nio.file.Files
import java.nio.file.Path

/**
 * A script for adding the line numbers to a snippet of code in the clipboard or from a file
 *
 * @author Craig Moore
 */
@Slf4j
class AddLineNumbers {
    public static final String SCRIPT_NAME = "add-line-numbers"

    static class Options {
        @Option(name = '--help', aliases = ['-h'], usage = "Show this help menu", required = false)
        boolean help = false

        @Option(name = '--inFile', aliases = ['-f'], usage = 'The file to read in and add line numbers to', required = false)
        String inFilePath

        @Option(name = '--outFile', aliases = ['-o'],
                usage = 'The file to write the output text to (by default the output is saved to the clipboard)',
                required = false)
        String outFilePath

        @Option(name = '--start', aliases = ['-n'], usage = "The starting line number of the code snippet", required = false)
        int start = 0

        @Option(name = '--debug', aliases = ['-d'], usage = "Print debug output", required = false)
        boolean debug = false
    }

    public final ClipboardUtils clipboardUtils

    AddLineNumbers(ClipboardUtils clipboardUtils) {
        this.clipboardUtils = clipboardUtils
    }
    AddLineNumbers() {
        this(new ClipboardUtils())
    }

    void run(Options options) {
        def codeSnippet
        if (options.inFilePath) {
            def inFile = Path.of(options.inFilePath)
            if (!Files.exists(inFile)) {
                throw new FileNotFoundException("No file found at path '$options.inFilePath'")
            }
            log.info "Reading text from file '$options.inFilePath'"
            codeSnippet = inFile.readLines()
        } else {
            codeSnippet = clipboardUtils.getClipboardContents()
        }

        StringBuilder numberedCodeSnippetBuilder = new StringBuilder()
        def start = 1
        if (options.start != 0) {
            start = options.start
            log.debug "Using starting number of $start"
        }
        int padding = String.valueOf(start + codeSnippet.size() - 1).length()
        codeSnippet.eachWithIndex { String line, int i ->
            def lineNumber = (i + start).toString()
            numberedCodeSnippetBuilder.append(padLeft(lineNumber, padding)).append(" | ").append(line).append("\n")
        }

        def numberCodeSnippet = numberedCodeSnippetBuilder.toString()
        if (options.debug) {
            ColorLogger.messageBuilder().cyan(numberCodeSnippet).log()
        }

        if (options.outFilePath) {
            def outFile = Path.of(options.outFilePath)
            Files.writeString(outFile, numberCodeSnippet)
            ColorLogger.messageBuilder().green( "Saved output to '").cyan(options.outFilePath).green("'").log()
        } else {
            clipboardUtils.setClipboardContents(numberCodeSnippet)
            ColorLogger.info "Saved output to clipboard"
        }
    }

    static String padLeft(String input, int length, String paddingChar = ' ') {
        if (!input) {
            // Pad with the padding character if input is null.
            return paddingChar.toString() * length
        }
        if (input.length() >= length) {
            log.warn("Truncating string '$input' because it's length ${input.size()} is longer than the expected length $length")
            // Truncate if longer than desired length.
            return input.substring(0, length)
        }
        return input.padLeft(length, paddingChar)
    }
}


