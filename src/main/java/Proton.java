import java.util.Scanner;

/**
 * Runs the Proton chatbot and manages the user's task list.
 */
public class Proton {
    public static void main(String[] args) {
        String banner = " ____            _              \n"
                + "|  _ \\ _ __ ___ | |_ ___  _ __ \n"
                + "| |_) | '__/ _ \\| __/ _ \\| '_ \\\n"
                + "|  __/| | | (_) | || (_) | | | |\n"
                + "|_|   |_|  \\___/ \\__\\___/|_| |_|\n";

        String SEPERATOR = "____________________________________________________________";

        System.out.println(banner);
        System.out.println(SEPERATOR);
        System.out.println("Hey there! I'm Proton, your positively charged chatbot!");
        System.out.println("I'm fired up and ready to help! What awesome thing shall we tackle today?");
        System.out.println(SEPERATOR);

        Scanner scanner = new Scanner(System.in);

        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String inputCommand = scanner.nextLine();

            System.out.println(SEPERATOR);

            if (inputCommand.equals("bye")) {
                System.out.println(" Powering down for now, I'll see you next time!");
                System.out.println(SEPERATOR);
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
            } else {
                tasks[taskCount] = new Task(inputCommand);
                taskCount++;
                System.out.println(" added: " + inputCommand);
            }

            System.out.println(SEPERATOR);
        }

        scanner.close();

    }
}
