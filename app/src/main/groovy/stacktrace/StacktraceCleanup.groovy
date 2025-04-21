package stacktrace

import common.IOScript
import common.IOScriptImpl
import groovy.util.logging.Slf4j

/**
 * A class for reading a stacktrace from the clipboard/file and removing unimportant lines and putting it in the clipboard
 *
 * @author Craig Moore
 */
@Slf4j
class StacktraceCleanup {
    static final String SCRIPT_NAME = 'stacktrace-cleanup'
    static final ArrayList<String> LINE_PREFIXES = [
        'java.base/jdk',
        'java.base/java.lang.reflect',
        'java.base@',
        'java.lang.reflect',
        'jdk.internal.reflect',
        'org.junit.jupiter.engine',
        'org.junit.platform',
        'org.gradle.api.internal',
        'org.gradle.internal',
        'org.gradle.process.internal',
        'worker.org.gradle',
        'org.codehaus.groovy',
        'groovy.lang',
        'com.sun'
    ]

    private final IOScript ioScript
    StacktraceCleanup(IOScript ioScript) { this.ioScript = ioScript }
    StacktraceCleanup() { this(new IOScriptImpl()) }

    static class Options extends IOScript.Options {}

    void run(Options options) {
        def stacktrace = ioScript.readInput(log, options)
        def outputString = cleanupStacktrace(stacktrace)
        ioScript.writeOutput(options, outputString)
    }

    private static String cleanupStacktrace(List<String> stacktrace) {
        stacktrace = stacktrace.findAll { String line ->
            !LINE_PREFIXES.any { prefix ->
                line.matches("^\t+at ${prefix}.*") ||
                line.matches("^\t+at app//${prefix}.*") ||
                line.matches(/^\t+at java\.base.*?\/${prefix}.*/)
            }
        }
        stacktrace.join('\n')
    }
}
