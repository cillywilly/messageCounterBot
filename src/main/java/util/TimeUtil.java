package util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtil {

    public static final String DD_MM_YYYY_HH_MM = "dd.MM.yyyy HH:mm";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM);

    public static String getNow() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        return now.format(FORMATTER);
    }

}
