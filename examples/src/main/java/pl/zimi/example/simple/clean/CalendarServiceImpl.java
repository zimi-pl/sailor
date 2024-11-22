package pl.zimi.example.simple.clean;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;

public class CalendarServiceImpl implements CalendarService {

     public DayInfo get(String date) {
         LocalDate localDate = LocalDate.parse(date);
         DayOfWeek dayOfWeek = localDate.getDayOfWeek();
         DayInfo dayInfo = new DayInfo(localDate, dayOfWeek.name(), !Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(dayOfWeek));
         return dayInfo;
    }

}
