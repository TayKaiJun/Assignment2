package sample;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    public static DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
    public static void printLog(String log){
        System.out.println("[" + dtf.format(LocalDateTime.now())+ "]: " + log);
    }
}
