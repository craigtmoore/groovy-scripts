package stacktrace

import common.IOScript
import common.IOScriptImpl
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

/**
 * When Quarkus logs its messages in JSON, it will parse the exception stacktrace which makes it difficult to
 * parse in Intellij. This script converts the JSON back to a standard stacktrace format.
 */
@Slf4j
class JsonToStacktrace {
    static final String SCRIPT_NAME = 'json-to-stacktrace'

    private final IOScript ioScript
    JsonToStacktrace(IOScript ioScript) { this.ioScript = ioScript }
    JsonToStacktrace() { this(new IOScriptImpl()) }

    static class Options extends IOScript.Options {}

    void run(Options options) {
        def json = ioScript.readInput(log, options).join("\n")
        def stacktrace = convertToStacktrace(json)
        if (stacktrace.lines().count() < 20) {
            println stacktrace
        }
        ioScript.writeOutput(options, stacktrace)
    }

    List<String> extractFrames(exception) {
        List<String> lines = []
        if (exception.exceptionType) {
            def msg = exception.message ? ": ${exception.message}" : ""
            lines.add("${exception.exceptionType}${msg}")
        }
        if (exception.frames) {
            exception.frames.each { frame ->
                def className = frame['class'] ?: ''
                def method = frame['method'] ?: ''
                def line = frame.containsKey('line') ? frame['line'] : null
                if (line != null) {
                    lines.add("\tat ${className}.${method}(${className.tokenize('.').last()}.java:${line})")
                } else {
                    lines.add("\tat ${className}.${method}(Unknown Source)")
                }
            }
        }
        if (exception.causedBy?.exception) {
            lines << 'Caused by:'
            lines.addAll(extractFrames(exception.causedBy.exception))
        }
        return lines
    }

    String convertToStacktrace(String jsonString) {
        def json = new JsonSlurper().parseText(jsonString)
        def frames = extractFrames(json.exception)
        return frames.join('\n')
    }
}
