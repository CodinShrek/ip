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
        if (inputCommand.equals("bye")) {
            System.out.println(" Powering down for now, I'll see you next time!");
            return false;
        }

        if (inputCommand.equals("list")) {
            listTasks();
            return true;
        }

        if (inputCommand.startsWith("mark ")) {
            markTask(inputCommand);
            return true;
        }

        if (inputCommand.startsWith("unmark ")) {
            unmarkTask(inputCommand);
            return true;
        }

        processTaskCreationCommand(inputCommand);
        return true;
    }

    private void processTaskCreationCommand(String inputCommand) {
        if (inputCommand.startsWith("todo ")) {
            addTodo(inputCommand);
            return;
        }

        if (inputCommand.startsWith("deadline ")) {
            addDeadline(inputCommand);
            return;
        }

        if (inputCommand.startsWith("event ")) {
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
        Task task = getTaskFromCommand(inputCommand, "mark ");
        if (task == null) {
            return;
        }

        task.markAsDone();
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    private void unmarkTask(String inputCommand) {
        Task task = getTaskFromCommand(inputCommand, "unmark ");
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
        String description = inputCommand.substring("todo ".length());
        addTask(new Todo(description));
    }

    private void addDeadline(String inputCommand) {
        String deadlineDetails = inputCommand.substring("deadline ".length());
        String[] deadlineParts = deadlineDetails.split(" /by ", 2);
        addTask(new Deadline(deadlineParts[0], deadlineParts[1]));
    }

    private void addEvent(String inputCommand) {
        String eventDetails = inputCommand.substring("event ".length());
        String[] descriptionAndTimes = eventDetails.split(" /from ", 2);
        String[] startAndEndTimes = descriptionAndTimes[1].split(" /to ", 2);
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
