import java.util.Scanner;

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

        String[] tasks = new String[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String inputCommand = scanner.nextLine();

            System.out.println(SEPERATOR);

            if (inputCommand.equals("bye")) {
                System.out.println(" Powering down for now, I'll see you next time!");
                System.out.println(SEPERATOR);
                break;
            } else if (inputCommand.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = inputCommand;
                taskCount++;
                System.out.println(" added: " + inputCommand);
            }

            System.out.println(SEPERATOR);
        }

        scanner.close();

    }
}
