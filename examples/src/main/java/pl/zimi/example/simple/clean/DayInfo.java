package pl.zimi.example.simple.clean;

import java.time.LocalDate;

public class DayInfo {

    private final LocalDate date;
    private final String weekday;
    private final boolean isWorkingDay;

    public DayInfo(LocalDate date, String weekday, boolean isWorkingDay) {
        this.date = date;
        this.weekday = weekday;
        this.isWorkingDay = isWorkingDay;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getWeekday() {
        return weekday;
    }

    public boolean isWorkingDay() {
        return isWorkingDay;
    }


}
