package stacktrace

import common.ClipboardUtils
import common.ColorLogger
import groovy.util.logging.Slf4j
import org.kohsuke.args4j.Option

import java.nio.file.Files
import java.nio.file.Path

/**
 * A class for reading a stacktrace from the clipboard/file and removing unimportant lines and putting it in the clipboard
 *
 * @author Craig Moore
 */
@Slf4j
class StacktraceCleanup {
    public static final String SCRIPT_NAME = 'stacktrace-cleanup'

    private final List<String> linePrefixesToIgnore
    public final ClipboardUtils clipboardUtils

    StacktraceCleanup() {
        linePrefixesToIgnore = [
                'java.base/jdk',
                'java.base/java.lang.reflect',
                'org.junit.jupiter.engine',
                'org.junit.platform',
                'org.gradle.api.internal',
                'org.gradle.internal',
                'org.gradle.process.internal',
                'com.sun'
        ]
        clipboardUtils = new ClipboardUtils()
    }

    class Options {
        @Option(name = '--help', aliases = ['-h'], usage = "Show this help menu", required = false)
        boolean help = false

        @Option(name = '--inFile', aliases = ['-f'], usage = 'The file to read in and add line numbers to', required = false)
        String inFilePath

        @Option(name = '--outFile', aliases = ['-o'],
                usage = 'The file to write the output text to (by default the output is saved to the clipboard)',
                required = false)
        String outFilePath

        @Option(name = '--debug', aliases = ['-d'], usage = "Print debug output", required = false)
        boolean debug = false
    }

    void run(Options options) {
        def stacktrace
        if (options.inFilePath) {
            def inFile = Path.of(options.inFilePath)
            if (!Files.exists(inFile)) {
                throw new FileNotFoundException("No file found at path '$options.inFilePath'")
            }
            log.info "Reading text from file '$options.inFilePath'"
            stacktrace = inFile.readLines()
        } else {
            stacktrace = clipboardUtils.getClipboardContents()
        }

        stacktrace = stacktrace.findAll { String line ->
            return !linePrefixesToIgnore.any { prefix -> line.startsWith("\tat $prefix") }
        }

        if (options.debug) {
            stacktrace.each {
                println it
            }
        }

        def stacktraceString = stacktrace.join('\n')
        if (options.outFilePath) {
            def outFile = Path.of(options.outFilePath)
            Files.writeString(outFile, stacktraceString)
            ColorLogger.messageBuilder().green( "Saved output to '").cyan(options.outFilePath).green("'").log()
        } else {
            clipboardUtils.setClipboardContents(stacktraceString)
            ColorLogger.info "Saved output to clipboard"
        }
    }
}
