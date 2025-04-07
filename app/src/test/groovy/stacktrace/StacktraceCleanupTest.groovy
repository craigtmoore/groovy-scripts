package stacktrace

import common.IOScript
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

/**
 * Tests for the StacktraceCleanup class.
 *
 * @author Craig Moore
 */
class StacktraceCleanupTest extends Specification {

    def "run() should call ioScript for input, clean up the stacktrace, and call ioScript to write output"() {
        given:
        def options = new StacktraceCleanup.Options()
        def stacktraceContent = getResourceFileContents('stacktrace/stacktrace.txt')
        def ioScriptMock = Mock(IOScript)
        def stacktraceCleanup = new StacktraceCleanup(ioScriptMock)

        when:
        stacktraceCleanup.run(options)

        then:
        1 * ioScriptMock.readInput(_, _) >> stacktraceContent.readLines()
        // Capture and verify the output content
        1 * ioScriptMock.writeOutput(_, { actualOutput ->
            def expectedOutput = """org.opentest4j.AssertionFailedError: ConfigMap ei-config-server-tester not found ==> expected: not <null>
\tat app//org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:39)
\tat app//org.junit.jupiter.api.Assertions.fail(Assertions.java:134)
\tat app//org.junit.jupiter.api.AssertNotNull.failNull(AssertNotNull.java:47)
\tat app//org.junit.jupiter.api.AssertNotNull.assertNotNull(AssertNotNull.java:36)
\tat app//org.junit.jupiter.api.Assertions.assertNotNull(Assertions.java:308)
\tat app//com.agfa.helm.ConfigServerTest.updateConfiguration(ConfigServerTest.java:89)
\tat app//com.agfa.helm.ConfigServerTest.resetConfiguration(ConfigServerTest.java:50)
\tSuppressed: org.opentest4j.AssertionFailedError: ConfigMap ei-config-server-tester not found ==> expected: not <null>
\t\tat app//org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:39)
\t\tat app//org.junit.jupiter.api.Assertions.fail(Assertions.java:134)
\t\tat app//org.junit.jupiter.api.AssertNotNull.failNull(AssertNotNull.java:47)
\t\tat app//org.junit.jupiter.api.AssertNotNull.assertNotNull(AssertNotNull.java:36)
\t\tat app//org.junit.jupiter.api.Assertions.assertNotNull(Assertions.java:308)
\t\tat app//com.agfa.helm.ConfigServerTest.updateConfiguration(ConfigServerTest.java:89)
\t\tat app//com.agfa.helm.ConfigServerTest.resetConfiguration(ConfigServerTest.java:50)
\t\t... 62 more"""
            assert expectedOutput == actualOutput : "Unexpected output string passed to the ioScript"
        })
    }

    private static String getResourceFileContents(String resourceName) {
        def testClass = StacktraceCleanupTest
        def url = testClass.getResource("/" + resourceName)
        if (!url) {
            throw new RuntimeException("Failed to resolve file: " + resourceName)
        }
        try {
            def path = Paths.get(url.toURI())
            return new String(Files.readAllBytes(path))
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to convert URL to URI", e)
        }
    }
}
