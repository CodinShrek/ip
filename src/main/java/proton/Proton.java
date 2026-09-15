package proton;

import proton.exception.ProtonException;
import proton.task.Deadline;
import proton.task.Event;
import proton.task.Task;
import proton.task.Todo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the Proton chatbot and manages the user's task list.
 */
public class Proton {
    private static final Path SAVE_FILE = Path.of("data", "proton.txt");
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
        try {
            loadTasks();
        } catch (IOException | IllegalArgumentException exception) {
            System.out.println(" Proton could not load data/proton.txt. Check the file and restart.");
            return;
        }

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

    private void listTasks() {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    private void markTask(String inputCommand) throws ProtonException {
        Task task = tasks.get(getTaskIndexFromCommand(inputCommand, MARK_COMMAND));
        task.markAsDone();
        saveTasks();
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    private void unmarkTask(String inputCommand) throws ProtonException {
        Task task = tasks.get(getTaskIndexFromCommand(inputCommand, UNMARK_COMMAND));
        task.markAsNotDone();
        saveTasks();
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /**
     * Removes the selected task and reports its details and the remaining count.
     *
     * @throws ProtonException If the command does not identify an existing task.
     */
    private void deleteTask(String inputCommand) throws ProtonException {
        int taskIndex = getTaskIndexFromCommand(inputCommand, DELETE_COMMAND);
        Task removedTask = tasks.remove(taskIndex);
        saveTasks();
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removedTask);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Finds the zero-based index referenced by a command containing a one-based task number.
     *
     * @return The index of the matching task.
     * @throws ProtonException If the command does not contain an existing task number.
     */
    private int getTaskIndexFromCommand(String inputCommand, String command) throws ProtonException {
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

            return taskIndex;
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

        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Loads saved tasks in order before accepting commands. A missing file means a new task list.
     *
     * @throws IOException If an existing save file cannot be read.
     * @throws IllegalArgumentException If a saved task has an invalid format.
     */
    private void loadTasks() throws IOException {
        try {
            for (String line : Files.readAllLines(SAVE_FILE)) {
                tasks.add(parseSavedTask(line));
            }
        } catch (NoSuchFileException exception) {
            // There is no saved list on the first run.
        }
    }

    /**
     * Restores a task's type, details, and completion status from its saved display format.
     *
     * @throws IllegalArgumentException If the line does not describe a supported task.
     */
    private Task parseSavedTask(String line) {
        Matcher taskMatcher = Pattern.compile("\\[([TDE])\\]\\[([ X])\\] (.+)").matcher(line);
        if (!taskMatcher.matches()) {
            throw new IllegalArgumentException("Invalid saved task.");
        }

        String type = taskMatcher.group(1);
        String details = taskMatcher.group(3);
        Task task;
        if (type.equals("T")) {
            task = new Todo(details);
        } else if (type.equals("D")) {
            Matcher deadlineMatcher = Pattern.compile("(.+) \\(by: (.+)\\)").matcher(details);
            if (!deadlineMatcher.matches()) {
                throw new IllegalArgumentException("Invalid saved deadline.");
            }
            task = new Deadline(deadlineMatcher.group(1), deadlineMatcher.group(2));
        } else {
            Matcher eventMatcher = Pattern.compile("(.+) \\(from: (.+?) to: (.+)\\)").matcher(details);
            if (!eventMatcher.matches()) {
                throw new IllegalArgumentException("Invalid saved event.");
            }
            task = new Event(eventMatcher.group(1), eventMatcher.group(2), eventMatcher.group(3));
        }

        if (taskMatcher.group(2).equals("X")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Overwrites the UTF-8 save file with one displayed task per line, creating its directory if needed.
     *
     * @throws ProtonException If the updated list cannot be saved.
     */
    private void saveTasks() throws ProtonException {
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(SAVE_FILE)) {
                for (Task task : tasks) {
                    writer.write(task.toString());
                    writer.newLine();
                }
            }
        } catch (IOException exception) {
            throw new ProtonException("The task list changed, but Proton could not save it to " + SAVE_FILE + ".");
        }
    }
}
