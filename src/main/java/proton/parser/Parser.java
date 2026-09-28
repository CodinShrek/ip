package proton.parser;

import proton.exception.ProtonException;
import proton.task.Deadline;
import proton.task.Event;
import proton.task.Task;
import proton.task.Todo;

/**
 * Converts console input into commands without reading or changing application state.
 */
public class Parser {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND_PREFIX = MARK_COMMAND + " ";
    private static final String UNMARK_COMMAND_PREFIX = UNMARK_COMMAND + " ";
    private static final String DELETE_COMMAND_PREFIX = DELETE_COMMAND + " ";
    private static final String TODO_COMMAND_PREFIX = TODO_COMMAND + " ";
    private static final String DEADLINE_COMMAND_PREFIX = DEADLINE_COMMAND + " ";
    private static final String EVENT_COMMAND_PREFIX = EVENT_COMMAND + " ";
    private static final String FIND_COMMAND_PREFIX = FIND_COMMAND + " ";
    private static final String DEADLINE_DELIMITER = " /by ";
    private static final String EVENT_START_DELIMITER = " /from ";
    private static final String EVENT_END_DELIMITER = " /to ";

    /**
     * Parses a command using the existing case-sensitive keywords and delimiters.
     * Task numbers are parsed as integers; TaskList checks whether they exist.
     *
     * @throws ProtonException If the command or its arguments are malformed.
     */
    public ParsedCommand parse(String inputCommand) throws ProtonException {
        if (inputCommand.chars().anyMatch(value -> Character.isISOControl(value) && value != '\t')) {
            throw new ProtonException("Positive charge alert! Commands cannot contain control characters.");
        }
        if (inputCommand.isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! No command was detected. Please enter a command.");
        }

        if (inputCommand.equals(BYE_COMMAND)) {
            return new ParsedCommand(CommandType.BYE, 0, null, null);
        }
        if (inputCommand.equals(LIST_COMMAND)) {
            return new ParsedCommand(CommandType.LIST, 0, null, null);
        }
        if (inputCommand.equals(FIND_COMMAND) || inputCommand.startsWith(FIND_COMMAND_PREFIX)) {
            return new ParsedCommand(CommandType.FIND, 0, null, parseKeyword(inputCommand));
        }
        if (inputCommand.equals(MARK_COMMAND) || inputCommand.startsWith(MARK_COMMAND_PREFIX)) {
            return new ParsedCommand(CommandType.MARK, parseTaskNumber(inputCommand, MARK_COMMAND), null, null);
        }
        if (inputCommand.equals(UNMARK_COMMAND) || inputCommand.startsWith(UNMARK_COMMAND_PREFIX)) {
            return new ParsedCommand(CommandType.UNMARK, parseTaskNumber(inputCommand, UNMARK_COMMAND), null, null);
        }
        if (inputCommand.equals(DELETE_COMMAND) || inputCommand.startsWith(DELETE_COMMAND_PREFIX)) {
            return new ParsedCommand(CommandType.DELETE, parseTaskNumber(inputCommand, DELETE_COMMAND), null, null);
        }
        if (inputCommand.equals(TODO_COMMAND) || inputCommand.startsWith(TODO_COMMAND_PREFIX)) {
            return new ParsedCommand(CommandType.ADD, 0, parseTodo(inputCommand), null);
        }
        if (inputCommand.equals(DEADLINE_COMMAND) || inputCommand.startsWith(DEADLINE_COMMAND_PREFIX)) {
            return new ParsedCommand(CommandType.ADD, 0, parseDeadline(inputCommand), null);
        }
        if (inputCommand.equals(EVENT_COMMAND) || inputCommand.startsWith(EVENT_COMMAND_PREFIX)) {
            return new ParsedCommand(CommandType.ADD, 0, parseEvent(inputCommand), null);
        }

        throw new ProtonException(
                "Positive charge alert! That command is outside Proton's orbit.");
    }

    private String parseKeyword(String inputCommand) throws ProtonException {
        String keyword = inputCommand.substring(FIND_COMMAND.length()).trim();
        if (keyword.isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! Use: find KEYWORD");
        }

        return keyword;
    }

    /**
     * Parses the one-based task number in a command; TaskList checks whether it exists.
     *
     * @return The task number supplied by the user.
     * @throws ProtonException If the command does not contain an integer task number.
     */
    private int parseTaskNumber(String inputCommand, String command) throws ProtonException {
        String taskNumberText = inputCommand.substring(command.length()).trim();
        if (taskNumberText.isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! Use: " + command + " TASK_NUMBER");
        }

        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new ProtonException(
                    "Positive charge alert! Use: " + command + " TASK_NUMBER");
        }
    }

    private Task parseTodo(String inputCommand) throws ProtonException {
        String description = inputCommand.substring(TODO_COMMAND.length()).trim();
        if (description.isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! A todo needs a description.");
        }

        return new Todo(description);
    }

    private Task parseDeadline(String inputCommand) throws ProtonException {
        String deadlineDetails = inputCommand.substring(DEADLINE_COMMAND.length()).trim();
        String[] deadlineParts = deadlineDetails.split(DEADLINE_DELIMITER, 2);
        if (deadlineParts.length < 2
                || deadlineParts[0].isBlank()
                || deadlineParts[1].isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! Use: deadline DESCRIPTION /by DATE");
        }

        return new Deadline(deadlineParts[0], deadlineParts[1]);
    }

    private Task parseEvent(String inputCommand) throws ProtonException {
        String eventDetails = inputCommand.substring(EVENT_COMMAND.length()).trim();
        String[] descriptionAndTimes = eventDetails.split(EVENT_START_DELIMITER, 2);
        if (descriptionAndTimes.length < 2 || descriptionAndTimes[0].isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! Use: event DESCRIPTION /from START /to END");
        }

        String[] startAndEndTimes = descriptionAndTimes[1].split(EVENT_END_DELIMITER, 2);
        if (startAndEndTimes.length < 2
                || startAndEndTimes[0].isBlank()
                || startAndEndTimes[1].isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! Use: event DESCRIPTION /from START /to END");
        }

        return new Event(
                descriptionAndTimes[0], startAndEndTimes[0], startAndEndTimes[1]);
    }

    /**
     * Identifies the operation to execute after parsing succeeds.
     */
    public enum CommandType {
        BYE, LIST, FIND, MARK, UNMARK, DELETE, ADD
    }

    /**
     * Holds parsed arguments. Only Parser can construct commands, so each type
     * receives the arguments required by the corresponding operation.
     */
    public static class ParsedCommand {
        private final CommandType type;
        // Used only by MARK, UNMARK, and DELETE; zero is a placeholder otherwise.
        private final int taskNumber;
        // Used only by ADD; null for operations that do not create a task.
        private final Task task;
        // Used only by FIND; null for operations that do not search for tasks.
        private final String keyword;

        private ParsedCommand(CommandType type, int taskNumber, Task task, String keyword) {
            this.type = type;
            this.taskNumber = taskNumber;
            this.task = task;
            this.keyword = keyword;
        }

        public CommandType getType() {
            return type;
        }

        public int getTaskNumber() {
            return taskNumber;
        }

        public Task getTask() {
            return task;
        }

        public String getKeyword() {
            return keyword;
        }
    }
}
