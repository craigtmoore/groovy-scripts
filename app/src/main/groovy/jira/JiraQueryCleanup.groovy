package jira

import common.IOScript
import common.IOScriptImpl
import groovy.util.logging.Slf4j

/**
 * This class is used to clean up Jira queries by escaping special characters.
 *
 * @author Craig Moore
 */
@Slf4j
class JiraQueryCleanup {
    static final String SCRIPT_NAME = 'jira-query-cleanup'

    private final IOScript ioScript
    JiraQueryCleanup(IOScript ioScript) { this.ioScript = ioScript }
    JiraQueryCleanup() { this(new IOScriptImpl()) }

    static class Options extends IOScript.Options {}

    void run(Options options) {
        def jiraQuery = ioScript.readInput(log, options)
        def outputString = cleanupJiraQuery(jiraQuery)
        println outputString
        ioScript.writeOutput(options, outputString)
    }

    /**
     * Escapes special characters in a string for a JIRA search,
     * in JIRA the valid escape sequences are \', \", \t, \n, \r, \\, '\ ' and \uXXXX.
     */
    private static String cleanupJiraQuery(List<String> input) {
        if (input.size() != 1) {
            throw new IllegalArgumentException("Expected a single string input, but got: ${input.size()} strings")
        }
        def inputText = input.get(0)

        // Special characters in JIRA queries that are ignored
        def specialCharsToSkip = "()[].+*?|^\$\\"
        if (specialCharsToSkip.any { c -> inputText.contains("\\\\" + c) }) {
            log.warn("Input string already processed")
            return inputText
        }

        if (inputText.trim().startsWith("text ~ ")) {
            inputText = inputText.trim().substring("text ~ ".length())
        }
        if (inputText.startsWith("\"") || inputText.startsWith("'")) {
            inputText = inputText.substring(1)
        }
        if (inputText.endsWith("\"") || inputText.endsWith("'")) {
            inputText = inputText.substring(0, inputText.length() - 1)
        }

        // Quotation marks that should be escaped with a single backslash
        def quoteChars = "'\""
        def escaped = new StringBuilder()
        for (char c : inputText.toCharArray()) {
            if (specialCharsToSkip.contains(c.toString())) {
                escaped.append('?')
            } else if (quoteChars.contains(c.toString())) {
                escaped.append('\\')
                escaped.append(c)
            } else {
                escaped.append(c)
            }
        }
        escaped = "text ~ \"\\\"$escaped\\\"\"".toString()
        def result = escaped.toString().replaceAll("\\s+", " ")
        return result
    }

    
}
