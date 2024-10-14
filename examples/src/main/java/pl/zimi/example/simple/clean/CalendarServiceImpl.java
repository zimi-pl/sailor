package pl.zimi.example.simple.clean;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;

public class CalendarServiceImpl implements CalendarService {

     public DayInfo get(String date) {
         LocalDate localDate = LocalDate.parse(date);
         DayOfWeek dayOfWeek = localDate.getDayOfWeek();
         DayInfo dayInfo = DayInfo.builder()
                 .date(localDate)
                 .weekday(dayOfWeek.name())
                 .isWorkingDay(!Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(dayOfWeek))
                 .build();
         return dayInfo;
    }

}
