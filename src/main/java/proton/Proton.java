package proton;

import java.util.Scanner;

/**
 * Runs the Proton chatbot and manages the user's task list.
 */
public class Proton {
    /** Maximum number of tasks that Proton can store. */
    private static final int MAX_TASK_COUNT = 100;
    /** Banner displayed when Proton starts. */
    private static final String BANNER = " ____            _              \n"
            + "|  _ \\ _ __ ___ | |_ ___  _ __ \n"
            + "| |_) | '__/ _ \\| __/ _ \\| '_ \\\n"
            + "|  __/| | | (_) | || (_) | | | |\n"
            + "|_|   |_|  \\___/ \\__\\___/|_| |_|\n";
    /** Separator displayed around Proton's responses. */
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND_PREFIX = "mark ";
    private static final String UNMARK_COMMAND_PREFIX = "unmark ";
    private static final String TODO_COMMAND_PREFIX = "todo ";
    private static final String DEADLINE_COMMAND_PREFIX = "deadline ";
    private static final String EVENT_COMMAND_PREFIX = "event ";
    private static final String DEADLINE_DELIMITER = " /by ";
    private static final String EVENT_START_DELIMITER = " /from ";
    private static final String EVENT_END_DELIMITER = " /to ";

    /** Tasks stored by Proton. */
    private final Task[] tasks = new Task[MAX_TASK_COUNT];
    /** Number of tasks currently stored. */
    private int taskCount;

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
        if (inputCommand.equals(BYE_COMMAND)) {
            System.out.println(" Powering down for now, I'll see you next time!");
            return false;
        }

        if (inputCommand.equals(LIST_COMMAND)) {
            listTasks();
            return true;
        }

        if (inputCommand.startsWith(MARK_COMMAND_PREFIX)) {
            markTask(inputCommand);
            return true;
        }

        if (inputCommand.startsWith(UNMARK_COMMAND_PREFIX)) {
            unmarkTask(inputCommand);
            return true;
        }

        processTaskCreationCommand(inputCommand);
        return true;
    }

    private void processTaskCreationCommand(String inputCommand) {
        if (inputCommand.startsWith(TODO_COMMAND_PREFIX)) {
            addTodo(inputCommand);
            return;
        }

        if (inputCommand.startsWith(DEADLINE_COMMAND_PREFIX)) {
            addDeadline(inputCommand);
            return;
        }

        if (inputCommand.startsWith(EVENT_COMMAND_PREFIX)) {
            addEvent(inputCommand);
            return;
        }

        addGenericTask(inputCommand);
    }

    private void listTasks() {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
    }

    private void markTask(String inputCommand) {
        Task task = getTaskFromCommand(inputCommand, MARK_COMMAND_PREFIX);
        if (task == null) {
            return;
        }

        task.markAsDone();
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    private void unmarkTask(String inputCommand) {
        Task task = getTaskFromCommand(inputCommand, UNMARK_COMMAND_PREFIX);
        if (task == null) {
            return;
        }

        task.markAsNotDone();
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /**
     * Finds the task referenced by a command containing a one-based task number.
     *
     * @return The matching task, or {@code null} after displaying a validation error.
     */
    private Task getTaskFromCommand(String inputCommand, String commandPrefix) {
        String taskNumberText = inputCommand.substring(commandPrefix.length()).trim();
        try {
            int taskIndex = Integer.parseInt(taskNumberText) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                System.out.println(" That task number is not in your list.");
                return null;
            }

            return tasks[taskIndex];
        } catch (NumberFormatException exception) {
            System.out.println(" Please specify a task number, for example: "
                    + commandPrefix.trim() + " 2");
            return null;
        }
    }

    private void addTodo(String inputCommand) {
        String description = inputCommand.substring(TODO_COMMAND_PREFIX.length());
        addTask(new Todo(description));
    }

    private void addDeadline(String inputCommand) {
        String deadlineDetails = inputCommand.substring(DEADLINE_COMMAND_PREFIX.length());
        String[] deadlineParts = deadlineDetails.split(DEADLINE_DELIMITER, 2);
        addTask(new Deadline(deadlineParts[0], deadlineParts[1]));
    }

    private void addEvent(String inputCommand) {
        String eventDetails = inputCommand.substring(EVENT_COMMAND_PREFIX.length());
        String[] descriptionAndTimes = eventDetails.split(EVENT_START_DELIMITER, 2);
        String[] startAndEndTimes = descriptionAndTimes[1].split(EVENT_END_DELIMITER, 2);
        addTask(new Event(
                descriptionAndTimes[0], startAndEndTimes[0], startAndEndTimes[1]));
    }

    private void addTask(Task task) {
        tasks[taskCount] = task;
        taskCount++;

        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    private void addGenericTask(String inputCommand) {
        tasks[taskCount] = new Task(inputCommand);
        taskCount++;
        System.out.println(" added: " + inputCommand);
    }
}
