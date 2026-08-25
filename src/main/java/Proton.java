import java.util.Scanner;

public class Proton {
    public static void main(String[] args) {
        String banner = " ____            _              \n"
                + "|  _ \\ _ __ ___ | |_ ___  _ __ \n"
                + "| |_) | '__/ _ \\| __/ _ \\| '_ \\\n"
                + "|  __/| | | (_) | || (_) | | | |\n"
                + "|_|   |_|  \\___/ \\__\\___/|_| |_|\n";

        String Seperator = "____________________________________________________________";

        System.out.println(banner);
        System.out.println(Seperator);
        System.out.println("Hey there! I'm Proton, your positively charged chatbot!");
        System.out.println("I'm fired up and ready to help! What awesome thing shall we tackle today?");
        System.out.println(Seperator);
        System.out.println("Powering down for now, I'll see you next time!");

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String inputCommand = scanner.nextLine();

            System.out.println(Seperator);

            if (inputCommand.equals("bye")) {
                System.out.println("Powering down for now, I'll see you next time!");
                System.out.println(Seperator);
                break;
            }

            System.out.println(" " + inputCommand);
            System.out.println(Seperator);

        }

        scanner.close();

    }
}
