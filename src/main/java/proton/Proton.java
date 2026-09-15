package proton;

import proton.exception.ProtonException;
import proton.task.Deadline;
import proton.task.Event;
import proton.task.Task;
import proton.task.Todo;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the Proton chatbot and manages the user's task list.
 */
public class Proton {
    private static final String BANNER = " ____            _              \n"
            + "|  _ \\ _ __ ___ | |_ ___  _ __ \n"
            + "| |_) | '__/ _ \\| __/ _ \\| '_ \\\n"
            + "|  __/| | | (_) | || (_) | | | |\n"
            + "|_|   |_|  \\___/ \\__\\___/|_| |_|\n";
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND_PREFIX = MARK_COMMAND + " ";
    private static final String UNMARK_COMMAND_PREFIX = UNMARK_COMMAND + " ";
    private static final String TODO_COMMAND_PREFIX = TODO_COMMAND + " ";
    private static final String DEADLINE_COMMAND_PREFIX = DEADLINE_COMMAND + " ";
    private static final String EVENT_COMMAND_PREFIX = EVENT_COMMAND + " ";
    private static final String DEADLINE_DELIMITER = " /by ";
    private static final String EVENT_START_DELIMITER = " /from ";
    private static final String EVENT_END_DELIMITER = " /to ";

    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Starts Proton and processes commands from the standard input stream.
     *
     * @param args Command-line arguments, which Proton does not use.
     */
    public static void main(String[] args) {
        new Proton().run();
    }

    private void run() {
        printWelcomeMessage();

        Scanner scanner = new Scanner(System.in);
        boolean shouldContinue = true;
        while (shouldContinue && scanner.hasNextLine()) {
            String inputCommand = scanner.nextLine();

            System.out.println(SEPARATOR);
            shouldContinue = processCommand(inputCommand);
            System.out.println(SEPARATOR);
        }
        scanner.close();
    }

    private void printWelcomeMessage() {
        System.out.println(BANNER);
        System.out.println(SEPARATOR);
        System.out.println("Hey there! I'm Proton, your positively charged chatbot!");
        System.out.println("I'm fired up and ready to help! What awesome thing shall we tackle today?");
        System.out.println(SEPARATOR);
    }

    private boolean processCommand(String inputCommand) {
        try {
            return executeCommand(inputCommand);
        } catch (ProtonException exception) {
            System.out.println(" " + exception.getMessage());
            return true;
        }
    }

    private boolean executeCommand(String inputCommand) throws ProtonException {
        if (inputCommand.isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! No command was detected. Please enter a command.");
        }

        if (inputCommand.equals(BYE_COMMAND)) {
            System.out.println(" Powering down for now, I'll see you next time!");
            return false;
        }

        if (inputCommand.equals(LIST_COMMAND)) {
            listTasks();
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

    private void listTasks() {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    private void markTask(String inputCommand) throws ProtonException {
        Task task = getTaskFromCommand(inputCommand, MARK_COMMAND);
        task.markAsDone();
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    private void unmarkTask(String inputCommand) throws ProtonException {
        Task task = getTaskFromCommand(inputCommand, UNMARK_COMMAND);
        task.markAsNotDone();
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /**
     * Finds the task referenced by a command containing a one-based task number.
     *
     * @return The matching task.
     * @throws ProtonException If the command does not contain an existing task number.
     */
    private Task getTaskFromCommand(String inputCommand, String command) throws ProtonException {
        String taskNumberText = inputCommand.substring(command.length()).trim();
        if (taskNumberText.isBlank()) {
            throw new ProtonException(
                    "Positive charge alert! Use: " + command + " TASK_NUMBER");
        }

        try {
            int taskIndex = Integer.parseInt(taskNumberText) - 1;
            if (tasks.isEmpty()) {
                throw new ProtonException(
                        "Positive charge alert! There are no tasks in Proton's orbit yet.");
            }

            if (taskIndex < 0 || taskIndex >= tasks.size()) {
                throw new ProtonException(
                        "Positive charge alert! Choose a task number from 1 to " + tasks.size() + ".");
            }

            return tasks.get(taskIndex);
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

    private void addTask(Task task) {
        tasks.add(task);

        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }
}
