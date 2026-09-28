package proton;

import proton.exception.ProtonException;
import proton.storage.Storage;
import proton.task.Deadline;
import proton.task.Event;
import proton.task.Task;
import proton.task.TaskList;
import proton.task.Todo;
import proton.ui.Ui;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Runs the Proton chatbot and manages the user's task list.
 */
public class Proton {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
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
    private static final String DEADLINE_DELIMITER = " /by ";
    private static final String EVENT_START_DELIMITER = " /from ";
    private static final String EVENT_END_DELIMITER = " /to ";

    private final Ui ui = new Ui();
    private final Storage storage = new Storage(Path.of("data", "proton.txt"));
    private TaskList tasks = new TaskList();

    /**
     * Starts Proton and processes commands from the standard input stream.
     *
     * @param args Command-line arguments, which Proton does not use.
     */
    public static void main(String[] args) {
        new Proton().run();
    }

    private void run() {
        try (ui) {
            try {
                tasks = new TaskList(storage.load());
            } catch (IOException | IllegalArgumentException exception) {
                ui.showWelcome();
                ui.showLoadingError();
                return;
            }
            ui.showWelcome();

            boolean shouldContinue = true;
            while (shouldContinue && ui.hasNextCommand()) {
                String inputCommand = ui.readCommand();

                ui.showSeparator();
                shouldContinue = processCommand(inputCommand);
                ui.showSeparator();
            }
        }
    }

    /**
     * Restores both list membership and completion flags when a command cannot be saved.
     */
    private boolean processCommand(String inputCommand) {
        TaskList.Snapshot previousTasks = tasks.createSnapshot();

        try {
            return executeCommand(inputCommand);
        } catch (ProtonException exception) {
            tasks.restore(previousTasks);
            ui.showError(exception.getMessage());
            return true;
        }
    }

    private boolean executeCommand(String inputCommand) throws ProtonException {
        if (inputCommand.chars().anyMatch(value -> Character.isISOControl(value) && value != '\t')) {
            throw new ProtonException("Positive charge alert! Commands cannot contain control characters.");
        }
        if (inputCommand.isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! No command was detected. Please enter a command.");
        }

        if (inputCommand.equals(BYE_COMMAND)) {
            ui.showGoodbye();
            return false;
        }

        if (inputCommand.equals(LIST_COMMAND)) {
            ui.showTasks(tasks.asList());
            return true;
        }

        if (inputCommand.equals(MARK_COMMAND)
                || inputCommand.startsWith(MARK_COMMAND_PREFIX)) {
            markTask(inputCommand);
            return true;
        }

        if (inputCommand.equals(UNMARK_COMMAND)
                || inputCommand.startsWith(UNMARK_COMMAND_PREFIX)) {
            unmarkTask(inputCommand);
            return true;
        }

        if (inputCommand.equals(DELETE_COMMAND)
                || inputCommand.startsWith(DELETE_COMMAND_PREFIX)) {
            deleteTask(inputCommand);
            return true;
        }

        processTaskCreationCommand(inputCommand);
        return true;
    }

    private void processTaskCreationCommand(String inputCommand) throws ProtonException {
        if (inputCommand.equals(TODO_COMMAND)
                || inputCommand.startsWith(TODO_COMMAND_PREFIX)) {
            addTodo(inputCommand);
            return;
        }

        if (inputCommand.equals(DEADLINE_COMMAND)
                || inputCommand.startsWith(DEADLINE_COMMAND_PREFIX)) {
            addDeadline(inputCommand);
            return;
        }

        if (inputCommand.equals(EVENT_COMMAND)
                || inputCommand.startsWith(EVENT_COMMAND_PREFIX)) {
            addEvent(inputCommand);
            return;
        }

        throw new ProtonException(
                "Positive charge alert! That command is outside Proton's orbit.");
    }

    private void markTask(String inputCommand) throws ProtonException {
        Task task = tasks.mark(getTaskNumberFromCommand(inputCommand, MARK_COMMAND));
        saveTasks();
        ui.showTaskMarked(task);
    }

    private void unmarkTask(String inputCommand) throws ProtonException {
        Task task = tasks.unmark(getTaskNumberFromCommand(inputCommand, UNMARK_COMMAND));
        saveTasks();
        ui.showTaskUnmarked(task);
    }

    /**
     * Removes the selected task and reports its details and the remaining count.
     *
     * @throws ProtonException If the command does not identify an existing task.
     */
    private void deleteTask(String inputCommand) throws ProtonException {
        Task removedTask = tasks.delete(getTaskNumberFromCommand(inputCommand, DELETE_COMMAND));
        saveTasks();
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Parses the one-based task number in a command; TaskList checks whether it exists.
     *
     * @return The task number supplied by the user.
     * @throws ProtonException If the command does not contain an integer task number.
     */
    private int getTaskNumberFromCommand(String inputCommand, String command) throws ProtonException {
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

    private void addTodo(String inputCommand) throws ProtonException {
        String description = inputCommand.substring(TODO_COMMAND.length()).trim();
        if (description.isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! A todo needs a description.");
        }

        addTask(new Todo(description));
    }

    private void addDeadline(String inputCommand) throws ProtonException {
        String deadlineDetails = inputCommand.substring(DEADLINE_COMMAND.length()).trim();
        String[] deadlineParts = deadlineDetails.split(DEADLINE_DELIMITER, 2);
        if (deadlineParts.length < 2
                || deadlineParts[0].isBlank()
                || deadlineParts[1].isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! Use: deadline DESCRIPTION /by DATE");
        }

        addTask(new Deadline(deadlineParts[0], deadlineParts[1]));
    }

    private void addEvent(String inputCommand) throws ProtonException {
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

        addTask(new Event(
                descriptionAndTimes[0], startAndEndTimes[0], startAndEndTimes[1]));
    }

    private void addTask(Task task) throws ProtonException {
        tasks.add(task);
        saveTasks();

        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Saves the current list without truncating the previous file on a failed write.
     *
     * @throws ProtonException If storage fails; the command handler restores the previous list.
     */
    private void saveTasks() throws ProtonException {
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            throw new ProtonException("Could not save data/proton.txt. No tasks were changed. "
                    + "Check the path and permissions, then retry or restart.");
        }
    }
}
