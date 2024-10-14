package pl.zimi.example.simple.clean;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class DayInfo {

    private final LocalDate date;
    private final String weekday;
    private final boolean isWorkingDay;
}
