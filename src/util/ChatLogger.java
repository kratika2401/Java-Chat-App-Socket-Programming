package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatLogger {

    private static final String LOG_FILE = "chat.log";

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static synchronized void log(String message) {

        try (PrintWriter writer = new PrintWriter(
                new FileWriter(LOG_FILE, true))) {

            writer.println(
                    "[" +
                            LocalDateTime.now().format(formatter) +
                            "] " +
                            message
            );

        }

        catch (IOException e) {

            System.out.println("Unable to write log file.");

            e.printStackTrace();

        }

    }

}