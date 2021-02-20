package Game;

import java.util.Scanner;


public class testRoom {

    public static void main(String[] args) {
        StringBuilder input_as_string = new StringBuilder();
        String text;
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            text = scanner.nextLine();
            input_as_string.append(text);
        }
        scanner.close();

        //parse input

    }
}
