package Command;

import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class ConsoleCommand {
    static public int inputNumber() {
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        return scanner.nextInt();

    }

    public static String dateOut() {
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", new Locale("ru"));
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
}
