package pl.zimi.flashcards.strategy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ExpotentialMemorizationStrategy implements MemorizationStrategy {

    public Instant nextAfter(Integer numberOfSuccesses, Instant now) {
        return now.plus(5 * (long)Math.pow(2, numberOfSuccesses), ChronoUnit.MINUTES);
    }
}
