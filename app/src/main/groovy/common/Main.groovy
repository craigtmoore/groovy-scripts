package common

import file.AddLineNumbers
import jira.JiraQueryCleanup
import org.codehaus.groovy.runtime.StackTraceUtils
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.ParserProperties
import stacktrace.StacktraceCleanup

/**
 * Main entry point for the scripts
 */
class Main {

    static File logFile

    static Map commands = [
        (AddLineNumbers.SCRIPT_NAME)   : AddLineNumbers,
        (StacktraceCleanup.SCRIPT_NAME): StacktraceCleanup,
        (JiraQueryCleanup.SCRIPT_NAME) : JiraQueryCleanup
    ]

    static void main(String[] args) {
        def scriptName = args.length > 0 ? args[0] : null
        if (!scriptName) {
            ColorLogger.error('Missing \'scriptName\' parameter')
            System.exit(1)
        }

        Class klass = scriptName != null ? commands[scriptName] : null

        if (!klass) {
            ColorLogger.messageBuilder().red("Failed to find script '").yellow(scriptName).red("'").log()
            ColorLogger.info "Available commands are: " + commands.keySet().join("\n- ")
            throw new IllegalArgumentException("Unknown script '$scriptName'")
        }

        def optionsClass = Class.forName(klass.name + '$Options')

        def options = optionsClass.newInstance()
        def parser = new CmdLineParser(options, ParserProperties.defaults().withUsageWidth(120))

        try {
            def scriptArgs = args.length > 1 ? args[1..-1] : []
            parser.parseArgument(scriptArgs)
            if (scriptArgs.contains('--help') || scriptArgs.contains('-h')) {
                ColorLogger.messageBuilder().green("Showing usage for ").blue(scriptName).green(":").log()
                parser.printUsage (System.out)
                return
            }
        } catch (CmdLineException e) {
            StackTraceUtils.deepSanitize(e)
            println e.getMessage()
            parser.printUsage(System.out)
            System.exit(-1)
        }

        try {
            klass.getDeclaredConstructor().newInstance().run(options)
        } catch (Throwable e) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace()
            System.exit(-1)
        }
    }
}
