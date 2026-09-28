package proton;

import proton.exception.ProtonException;
import proton.parser.Parser;
import proton.parser.Parser.ParsedCommand;
import proton.storage.Storage;
import proton.task.Task;
import proton.task.TaskList;
import proton.ui.Ui;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Runs the Proton chatbot and manages the user's task list.
 */
public class Proton {
    private final Parser parser = new Parser();
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
            return executeCommand(parser.parse(inputCommand));
        } catch (ProtonException exception) {
            tasks.restore(previousTasks);
            ui.showError(exception.getMessage());
            return true;
        }
    }

    private boolean executeCommand(ParsedCommand command) throws ProtonException {
        switch (command.getType()) {
        case BYE:
            ui.showGoodbye();
            return false;
        case LIST:
            ui.showTasks(tasks.asList());
            break;
        case MARK:
            markTask(command.getTaskNumber());
            break;
        case UNMARK:
            unmarkTask(command.getTaskNumber());
            break;
        case DELETE:
            deleteTask(command.getTaskNumber());
            break;
        case ADD:
            addTask(command.getTask());
            break;
        default:
            throw new IllegalStateException("Unhandled command type: " + command.getType());
        }
        return true;
    }

    private void markTask(int taskNumber) throws ProtonException {
        Task task = tasks.mark(taskNumber);
        saveTasks();
        ui.showTaskMarked(task);
    }

    private void unmarkTask(int taskNumber) throws ProtonException {
        Task task = tasks.unmark(taskNumber);
        saveTasks();
        ui.showTaskUnmarked(task);
    }

    /**
     * Removes the selected task and reports its details and the remaining count.
     *
     * @throws ProtonException If the command does not identify an existing task.
     */
    private void deleteTask(int taskNumber) throws ProtonException {
        Task removedTask = tasks.delete(taskNumber);
        saveTasks();
        ui.showTaskDeleted(removedTask, tasks.size());
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
