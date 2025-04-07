package file


import common.IOScript
import spock.lang.Specification

/**
 * Tests for the AddLineNumbersTest class
 *
 * @author Craig Moore
 */
class AddLineNumbersTest extends Specification {

    def "run() should call ioScript for input, add line numbers, and call ioScript to write output"() {
        given: // The ioScript is mocked to return the input lines
        def ioScriptMock = Mock(IOScript)
        1 * ioScriptMock.readInput(_, _) >> ["line1", "line2", "line3"]

        def options = new AddLineNumbers.Options()
        def addLineNumbers = new AddLineNumbers(ioScriptMock)

        when: // The script is run
        addLineNumbers.run(options)

        then: // The mocked ioScript is called to write the output
        1 * ioScriptMock.writeOutput(_, "1  | line1\n2  | line2\n3  | line3\n")
    }
}