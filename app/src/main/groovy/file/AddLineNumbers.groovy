package file

import common.ClipboardUtils
import common.ColorLogger
import common.IOScript
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
class AddLineNumbers extends IOScript {
    public static final String SCRIPT_NAME = "add-line-numbers"

    static class Options extends IOScript.Options {
        @Option(name = '--start', aliases = ['-n'], usage = "The starting line number of the code snippet", required = false)
        int start = 0
    }

    void run(Options options) {
        def lines = readInput(log, options)
        def numberedString = addLineNumbers(options, lines)
        writeOutput(options, numberedString)
    }

    static String addLineNumbers(Options options, List<String> lines) {
        StringBuilder numberedCodeSnippetBuilder = new StringBuilder()
        def start = 1
        if (options.start != 0) {
            start = options.start
            log.debug "Using starting number of $start"
        }
        int padding = String.valueOf(start + lines.size() - 1).length()
        lines.eachWithIndex { String line, int i ->
            def lineNumber = (i + start).toString()
            numberedCodeSnippetBuilder.append(padLeft(lineNumber, padding)).append("  | ").append(line).append("\n")
        }
        numberedCodeSnippetBuilder.toString()
    }

    private static String padLeft(String input, int length, String paddingChar = ' ') {
        if (!input) {
            // Pad with the padding character if input is null.
            return paddingChar.toString() * length
        }
        if (input.length() > length) {
            log.warn("Truncating string '$input' because it's length ${input.size()} is longer than the expected length $length")
            // Truncate if longer than desired length.
            return input.substring(0, length)
        }
        return input.padLeft(length, paddingChar)
    }
}


