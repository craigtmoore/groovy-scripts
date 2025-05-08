# groovy-scripts
## How to use

<div class="info-box">
  <strong>Note:</strong> Ensure that you have Java 21 installed and the JAVA_HOME environment variable set to it and added to your PATH.
</div>

<style>
.info-box {
  border: 1px solid #ADD8E6;
  padding: 10px;
  margin-bottom: 10px;
}
.info-box strong {
  font-weight: bold;
}
</style>

### List Available Commands
```
java -jar scripts.jar --help

Available commands are:
 - add-line-numbers
 - stacktrace-cleanup
 - jira-query-cleanup
 - test-case-count
Use <commandName> --help to get help for a specific command
```

### Example `add-line-numbers` help

Use the `--help` parameter with the `add-line-numbers` script to get more information about the options available for that script.

```
$ java -jar scripts.jar add-line-numbers --help

Showing usage for add-line-numbers:
 --clipboard (-cp)  : Read the input from the clipboard
 --debug (-d)       : Print debug output
 --help (-h)        : Show this help menu
 --inFile (-f) VAL  : The file to read in and add line numbers to
 --outFile (-o) VAL : The file to write the output text to (by default the output is saved to the clipboard)
 --start (-n) N     : The starting line number of the code snippet
 --update (-u)      : Inline update the file to read in (only works with the --inFile option)

```

### Example `add-line-numbers`

```
$ java -jar scripts.jar add-line-numbers -cp -n 26

2025-05-08 13:29:24 [main] INFO  file.AddLineNumbers - Reading text from the clipboard

26  |         def workingDir = Path.of(options.workingDir)
27  |         if (!Files.exists(workingDir)) {
28  |             throw new FileNotFoundException("Working directory does not exist: $workingDir")
29  |         }

Saved output to clipboard
```

## Current Scripts

* **add-line-numbers**
  * Adds line numbers to a code snippet
* **jira-query-cleanup**
  * Reads a JIRA query from the clipboard (or file) and cleans it up
* **test-case-count**
  * Given a directory of JUnit XML files, counts the number of test cases in each file and outputs the total count
* **stacktrace-cleanup**
  * Removes unnecessary lines from a stack trace
