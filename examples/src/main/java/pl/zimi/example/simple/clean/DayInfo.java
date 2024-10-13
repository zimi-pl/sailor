package pl.zimi.example.simple.clean;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Builder
@Data
public class DayInfo {

    LocalDate date;
    String weekday;
    boolean isWorkingDay;
}
