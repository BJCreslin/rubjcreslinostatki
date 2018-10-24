package Command;

import java.io.InputStreamReader;
import java.util.Scanner;

public class ConsoleCommand {
    static public int inputNumber() {
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        return scanner.nextInt();

    }
}
