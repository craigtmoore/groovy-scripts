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
        // Characters to escape in regex
        def specialChars = "'\""
        def escaped = new StringBuilder()
        for (char c : input.get(0).toCharArray()) {
            if (specialChars.contains(c.toString())) {
                escaped.append('\\')
            }
            escaped.append(c)
        }
        return escaped.toString()
    }
    
    
}
