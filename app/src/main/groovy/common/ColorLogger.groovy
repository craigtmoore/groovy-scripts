package common
/**
 * Colorized standard output logger
 *
 * @author Craig Moore
 */
class ColorLogger {
    static final String RESET = "\u001B[0m"
    static final String RED = "\u001B[31m"
    static final String GREEN = "\u001B[32m"
    static final String YELLOW = "\u001B[33m"
    static final String CYAN = "\u001B[36m"
    static final String GREY = "\u001B[90m"
    static final String BLUE = '\u001B[34m'

    static void info(String message) {
        messageBuilder().green(message).log()
    }

    static void warning(String message) {
        messageBuilder().yellow(message).log()
    }

    static void error(String message) {
        messageBuilder().red(message).log()
    }

    static void debug(String message) {
        messageBuilder().cyan(message).log()
    }

    static void trace(String message) {
        messageBuilder().grey(message).log()
    }

    static MessageBuilder messageBuilder() {
        return new MessageBuilder()
    }

    static class MessageBuilder {

        private StringBuilder stringBuilder = new StringBuilder()

        MessageBuilder customColor(String color, String text) {
            stringBuilder.append(color).append(text).append(RESET)
            return this
        }
        MessageBuilder red(String text) {
            stringBuilder.append(RED).append(text).append(RESET)
            return this
        }
        MessageBuilder green(String text) {
            stringBuilder.append(GREEN).append(text).append(RESET)
            return this
        }
        MessageBuilder yellow(String text) {
            stringBuilder.append(YELLOW).append(text).append(RESET)
            return this
        }
        MessageBuilder cyan(String text) {
            stringBuilder.append(CYAN).append(text).append(RESET)
            return this
        }
        MessageBuilder grey(String text) {
            stringBuilder.append(GREY).append(text).append(RESET)
            return this
        }
        MessageBuilder blue(String text) {
            stringBuilder.append(BLUE).append(text).append(RESET)
            return this
        }
        String build() {
            return stringBuilder.toString()
        }
        void log() {
            println stringBuilder.toString()
        }
    }
}
