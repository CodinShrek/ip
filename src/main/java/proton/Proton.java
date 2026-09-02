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

    /**
     * Starts Proton and processes commands from the standard input stream.
     *
     * @param args Command-line arguments, which Proton does not use.
     */
    public static void main(String[] args) {
        System.out.println(BANNER);
        System.out.println(SEPARATOR);
        System.out.println("Hey there! I'm Proton, your positively charged chatbot!");
        System.out.println("I'm fired up and ready to help! What awesome thing shall we tackle today?");
        System.out.println(SEPARATOR);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASK_COUNT];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String inputCommand = scanner.nextLine();

            System.out.println(SEPARATOR);

            if (inputCommand.equals("bye")) {
                System.out.println(" Powering down for now, I'll see you next time!");
                System.out.println(SEPARATOR);
                break;
            } else if (inputCommand.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (inputCommand.startsWith("mark ")) {
                String taskNumberText = inputCommand.substring("mark ".length()).trim();

                try {
                    int taskIndex = Integer.parseInt(taskNumberText) - 1;
                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println(" That task number is not in your list.");
                    } else {
                        tasks[taskIndex].markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Please specify a task number, for example: mark 2");
                }
            } else if (inputCommand.startsWith("unmark ")) {
                String taskNumberText = inputCommand.substring("unmark ".length()).trim();

                try {
                    int taskIndex = Integer.parseInt(taskNumberText) - 1;
                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println(" That task number is not in your list.");
                    } else {
                        tasks[taskIndex].markAsNotDone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Please specify a task number, for example: unmark 2");
                }
            } else if (inputCommand.startsWith("todo ")) {
                String description = inputCommand.substring("todo ".length());
                Task todo = Task.createTodo(description);
                tasks[taskCount] = todo;
                taskCount++;

                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + todo);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else if (inputCommand.startsWith("deadline ")) {
                String deadlineDetails = inputCommand.substring("deadline ".length());
                String[] deadlineParts = deadlineDetails.split(" /by ", 2);
                Task deadline = Task.createDeadline(deadlineParts[0], deadlineParts[1]);
                tasks[taskCount] = deadline;
                taskCount++;

                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + deadline);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else {
                tasks[taskCount] = new Task(inputCommand);
                taskCount++;
                System.out.println(" added: " + inputCommand);
            }

            System.out.println(SEPARATOR);
        }

        scanner.close();
    }
}
