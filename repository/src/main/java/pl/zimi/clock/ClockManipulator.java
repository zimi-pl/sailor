package pl.zimi.clock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class ClockManipulator {

    public static ClockManipulator managable() {
        return new ClockManipulator(Instant.now());
    }

    public static ClockManipulator managable(Instant initialDate) {
        return new ClockManipulator(initialDate);
    }

    private final Clock clock;

    private Instant currentTime;

    private ClockManipulator(Instant initialDate) {
        this.currentTime = initialDate;
        this.clock = new Clock() {
            @Override
            public ZoneId getZone() {
                return null;
            }

            @Override
            public Clock withZone(ZoneId zone) {
                return null;
            }

            @Override
            public Instant instant() {
                return currentTime;
            }
        };
    }

    public Clock getClock() {
        return clock;
    }

    public void addMinutes(int amount) {
        currentTime = currentTime.plus(amount, ChronoUnit.MINUTES);
    }

    public void addHours(int amount) {
        currentTime = currentTime.plus(amount, ChronoUnit.HOURS);
    }

    public void addSeconds(int amount) {
        currentTime = currentTime.plus(amount, ChronoUnit.SECONDS);
    }
}
