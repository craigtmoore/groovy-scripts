package common

import org.slf4j.Logger
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

/**
 * Tests for the IOScriptImpl class.
 *
 * @author Craig Moore
 */
class IOScriptImplTest extends Specification {

    def "readInput() should throw an exception if the clipboard contents is blank"() {
        given: // An IOScriptImpl instance with a mocked ClipboardAccessor and logger and the useClipboard option is true
        def logMock = Mock(Logger)
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def options = new IOScript.Options(useClipboard: true)
        def ioScript = new IOScriptImpl(clipboardAccessorMock)

        when: // Call the readInput method is called with the mocked logger and options
        ioScript.readInput(logMock, options)

        then: // Verify that the clipboard contents are retrieved and an exception is thrown
        1 * clipboardAccessorMock.getClipboardContents() >> []
        1 * logMock.info("Reading text from the clipboard")
        def exception = thrown(IllegalStateException)
        exception.message == 'No input provided'
    }

    def "readInput() should throw an exception if the file contents are blank"() {
        given: // An IOScriptImpl instance with a mocked logger and an empty input file
        def logMock = Mock(Logger)
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def inFile = File.createTempFile('inFile', 'txt')
        inFile.write("")
        def options = new IOScript.Options(inFilePath: inFile.getPath())
        def ioScript = new IOScriptImpl(clipboardAccessorMock)

        when: // Call the readInput method is called with the mocked logger and options
        ioScript.readInput(logMock, options)

        then: // Verify that the clipboard contents are retrieved and an exception is thrown
        0 * clipboardAccessorMock.getClipboardContents()
        1 * logMock.info("Reading text from file '${inFile.getPath()}'")
        def exception = thrown(IllegalStateException)
        exception.message == 'No input provided'
    }

    def "readInput() should throw a FileNotFoundException if the file does not exist"() {
        given: // An IOScriptImpl instance with a mocked logger and a non-existent input file path
        def logMock = Mock(Logger)
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def options = new IOScript.Options(inFilePath: 'nonExistentFile.txt')
        def ioScript = new IOScriptImpl(clipboardAccessorMock)

        when: // Call the readInput method with the mocked logger and options
        ioScript.readInput(logMock, options)

        then: // Verify that a FileNotFoundException is thrown
        def exception = thrown(FileNotFoundException)
        exception.message == "No file found at path 'nonExistentFile.txt'"
    }

    def "readInput() should read from the input file when the `inFilePath` option is set"() {
        given: // An IOScriptImpl instance with a mocked logger and a non-empty input file
        def logMock = Mock(Logger)
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def inFile = File.createTempFile('inFile', 'txt')
        inFile.write("line1\nline2\nline3")
        def options = new IOScript.Options(inFilePath: inFile.getPath())
        def ioScript = new IOScriptImpl(clipboardAccessorMock)

        when: // Call the readInput method with the mocked logger and options
        def inputLines = ioScript.readInput(logMock, options)

        then: // Verify that the input is read from the file
        0 * clipboardAccessorMock.getClipboardContents()
        1 * logMock.info("Reading text from file '${inFile.getPath()}'")
        inputLines == ["line1", "line2", "line3"]
    }

    def "readInput() should set outFilePath to inFilePath when updateInFile is true"() {
        given: // An IOScriptImpl instance with a mocked logger and a non-empty input file
        def logMock = Mock(Logger)
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def inFile = File.createTempFile('inFile', 'txt')
        inFile.write("line1\nline2\nline3")
        def options = new IOScript.Options(inFilePath: inFile.getPath(), updateInFile: true)
        def ioScript = new IOScriptImpl(clipboardAccessorMock)

        when: // Call the readInput method with the mocked logger and options
        def inputLines = ioScript.readInput(logMock, options)

        then: // Verify that the input is read from the file and outFilePath is set to inFilePath
        0 * clipboardAccessorMock.getClipboardContents()
        1 * logMock.info("Reading text from file '${inFile.getPath()}'")
        1 * logMock.debug('Observed --update flag, so setting option.outFilePath to option.inFilePath')
        inputLines == ["line1", "line2", "line3"]
        options.outFilePath == options.inFilePath
    }

    def "readInput() should read from the clipboard when the `useClipboard` option is true"() {
        given: // An IOScriptImpl instance with a mocked ClipboardAccessor and logger, and the useClipboard option is true
        def logMock = Mock(Logger)
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def options = new IOScript.Options(useClipboard: true)
        def ioScript = new IOScriptImpl(clipboardAccessorMock)
        def clipboardContent = ["line1", "line2", "line3"]

        when: // Call the readInput method with the mocked logger and options
        def inputLines = ioScript.readInput(logMock, options)

        then: // Verify that the input is read from the clipboard
        1 * clipboardAccessorMock.getClipboardContents() >> clipboardContent
        1 * logMock.info("Reading text from the clipboard")
        inputLines == clipboardContent
    }

    def "readInput() should read from standard input when `useClipboard=false` and `inFilePath` is not set"() {
        given: // An IOScriptImpl instance with a mocked logger and standard input
        def logMock = Mock(Logger)
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def options = new IOScript.Options()
        def ioScript = new IOScriptImpl(clipboardAccessorMock)
        def inputText = "line1\nline2\nline3"
        InputStream originalIn = System.in
        System.setIn(new ByteArrayInputStream(inputText.bytes))

        when: // Call the readInput method with the mocked logger and options
        def inputLines = ioScript.readInput(logMock, options)

        then: // Verify that the input is read from standard input
        0 * clipboardAccessorMock.getClipboardContents()
        1 * logMock.info("Reading text from standard input")
        inputLines == ["line1", "line2", "line3"]

        cleanup:
        System.setIn(originalIn)
    }

    def "writeOutput() should write to the specified output file"() {
        given: // An IOScriptImpl instance with a mocked ClipboardAccessor and options with outFilePath set
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def options = new IOScript.Options(outFilePath: 'output.txt')
        def ioScript = new IOScriptImpl(clipboardAccessorMock)
        def outputString = "line1\nline2\nline3"
        def outFile = Path.of(options.outFilePath)

        when: // Call the writeOutput method with the options and outputString
        ioScript.writeOutput(options, outputString)

        then: // Verify that the output is written to the file
        Files.readString(outFile) == outputString

        cleanup:
        Files.deleteIfExists(outFile)
    }

    def "writeOutput() should write to the clipboard when useClipboard is true"() {
        given: // An IOScriptImpl instance with a mocked ClipboardAccessor and options with useClipboard set to true
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def options = new IOScript.Options(useClipboard: true)
        def ioScript = new IOScriptImpl(clipboardAccessorMock)
        def outputString = "line1\nline2\nline3"

        when: // Call the writeOutput method with the options and outputString
        ioScript.writeOutput(options, outputString)

        then: // Verify that the output is written to the clipboard
        1 * clipboardAccessorMock.setClipboardContents(outputString)
    }

    def "writeOutput() should print to standard output when no outFilePath or useClipboard is set"() {
        given: // An IOScriptImpl instance with a mocked ClipboardAccessor and options with no outFilePath or useClipboard
        def clipboardAccessorMock = Mock(ClipboardAccessor)
        def options = new IOScript.Options()
        def ioScript = new IOScriptImpl(clipboardAccessorMock)
        def outputString = "line1\nline2\nline3"
        def originalOut = System.out
        def outputStream = new ByteArrayOutputStream()
        System.setOut(new PrintStream(outputStream))

        when: // Call the writeOutput method with the options and outputString
        ioScript.writeOutput(options, outputString)

        then: // Verify that the output is printed to standard output
        outputStream.toString().trim() == outputString

        cleanup:
        System.setOut(originalOut)
    }


}
