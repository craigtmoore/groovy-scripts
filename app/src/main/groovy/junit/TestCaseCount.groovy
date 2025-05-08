package junit

import common.IOScript
import groovy.util.logging.Slf4j
import groovy.xml.XmlSlurper
import org.kohsuke.args4j.Option

import java.nio.file.Files
import java.nio.file.Path

/**
 * Class used to count the number of test cases in all JUnit XML files in a given directory.
 *
 * @author craig.moore@agfa.com - aptng
 */
@Slf4j
class TestCaseCount {
    static final String SCRIPT_NAME = 'test-case-count';

    static class Options extends IOScript.Options {
        @Option(name = '--workDir', aliases = ['-wd'], usage = "The working directory to use", required = false)
        String workingDir = System.getProperty('user.dir')
    }

    static void run(Options options) {
        def workingDir = Path.of(options.workingDir)
        if (!Files.exists(workingDir)) {
            throw new FileNotFoundException("Working directory does not exist: $workingDir")
        }
        def numTestCases = 0
        Set<String> testNames = []
        Files.walk(workingDir).each { file ->
            if (file.fileName.toString().endsWith('.xml')) {
                log.debug "Processing file: $file"
                def testSuite = new XmlSlurper().parse(file)
                testSuite.testcase.each { testCase ->
                    String name = testCase.@name.text()
                    if (testNames.contains(name)) {
                        log.warn "Duplicate test case found: $name"
                    } else {
                        log.debug "Adding test case: $name"
                    }
                    testNames << name
                    numTestCases++
                }
            }
        }
        log.info "Total test cases: $numTestCases"
        log.info "Total unique test cases: ${testNames.size()}"
    }
}
