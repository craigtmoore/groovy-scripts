package common

import org.kohsuke.args4j.Option

/**
 * Abstract class for scripts that read input and write output (either from a file or the clipboard)
 *
 * @author craig.moore@agfa.com - aptng
 */
interface IOScript {

    static class Options {
        @Option(name = '--help', aliases = ['-h'], usage = "Show this help menu", required = false)
        boolean help = false

        @Option(name = '--inFile', aliases = ['-f'], usage = 'The file to read in and add line numbers to', required = false)
        String inFilePath

        @Option(name = '--update', aliases = ['-u'], usage = 'Inline update the file to read in (only works with the --inFile option)')
        boolean updateInFile = false

        @Option(name = '--outFile', aliases = ['-o'],
                usage = 'The file to write the output text to (by default the output is saved to the clipboard)',
                required = false)
        String outFilePath

        @Option(name = '--debug', aliases = ['-d'], usage = "Print debug output", required = false)
        boolean debug = false

        @Option(name = '--clipboard', aliases = ['-cp'], usage = 'Read the input from the clipboard', required = false)
        boolean useClipboard = false
    }

    List<String> readInput(log, Options options)

    void writeOutput(Options options, String outputString)
}
