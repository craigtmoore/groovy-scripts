package stacktrace

import common.ClipboardUtils
import common.IOScript
import groovy.util.logging.Slf4j

/**
 * A class for reading a stacktrace from the clipboard/file and removing unimportant lines and putting it in the clipboard
 *
 * @author Craig Moore
 */
@Slf4j
class StacktraceCleanup extends IOScript {
    public static final String SCRIPT_NAME = 'stacktrace-cleanup'

    private final List<String> linePrefixesToIgnore

    StacktraceCleanup() {
        linePrefixesToIgnore = [
                'java.base/jdk',
                'java.base/java.lang.reflect',
                'java.lang.reflect',
                'jdk.internal.reflect',
                'org.junit.jupiter.engine',
                'org.junit.platform',
                'org.gradle.api.internal',
                'org.gradle.internal',
                'org.gradle.process.internal',
                'com.sun'
        ]
    }

    static class Options extends IOScript.Options {}

    void run(Options options) {
        def stacktrace = readInput(log, options)
        def outputString = cleanupStacktrace(stacktrace)
        writeOutput(options, outputString)
    }

    private String cleanupStacktrace(List<String> stacktrace) {
        stacktrace = stacktrace.findAll { String line ->
            return !linePrefixesToIgnore.any { prefix ->
                line.startsWith("\tat $prefix") ||
                        line.startsWith("\tat app//$prefix") ||
                        line.matches(/^\tat java\.base.*?\/${prefix}.*/)
            }
        }
        stacktrace.join('\n')
    }
}
