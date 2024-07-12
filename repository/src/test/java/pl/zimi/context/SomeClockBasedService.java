package pl.zimi.context;

import java.time.Clock;

public class SomeClockBasedService {

    private final Clock clock;

    public SomeClockBasedService(Clock clock) {
        this.clock = clock;
    }
}
