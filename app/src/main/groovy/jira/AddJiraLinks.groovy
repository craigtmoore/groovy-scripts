package jira

import common.IOScript
import common.IOScriptImpl
import groovy.util.logging.Slf4j
import org.kohsuke.args4j.Option

@Slf4j
class AddJiraLinks {
    static final String SCRIPT_NAME = 'add-jira-links'
    public static final String JIRA_KEY_REGEX = "(IEI-\\d+|IEIBUILD-\\d+|IPTS-\\d+)"

    private final IOScript ioScript

    AddJiraLinks(IOScript ioScript) { this.ioScript = ioScript }
    AddJiraLinks() { this(new IOScriptImpl()) }

    static class Options extends IOScript.Options {
        @Option(name = '--html', usage = "Output as HTML formattedlinks (default: true)", required = false)
        boolean html = true


        @Option(name = '--markDown', usage = "Output as MarkDown formatted links (default: true)", required = false)
        boolean markDown = false


        @Option(name = '--jira', usage = "Output as Jira formatted links (default: true)", required = false)
        boolean jira = false
    }

    void run(Options options) {
        def input = ioScript.readInput(log, options)
        def outputString = addJiraLinks(input, options)
        ioScript.writeOutput(options, outputString)
    }

    static String addJiraLinks(List<String> inputString, Options options) {
        inputString.collect { line ->
            def trimmedIssue = line.trim()
            if (trimmedIssue) {
                if (options.markDown) {
                    line = line.replaceAll(JIRA_KEY_REGEX, "[\$1](http://jiraprod.agfahealthcare.com/browse/\$1)").trim();
                } else if (options.jira) {
                    line = line.replaceAll(JIRA_KEY_REGEX, "[\$1|http://jiraprod.agfahealthcare.com/browse/\$1]").trim();
                } else if (options.html) {
                    options.useHtml = true
                    line = line.replaceAll(JIRA_KEY_REGEX, "<a href=\\\"http://jiraprod.agfahealthcare.com/browse/\$1\\\">\$1</a>").trim();
                } else {
                    throw new IllegalArgumentException("At least one of --html, --markDown or --jira must be specified")
                }
                return line
            }
            return ""
        }
                .findAll { it } // Remove empty strings
                .join("\n")
    }
}
