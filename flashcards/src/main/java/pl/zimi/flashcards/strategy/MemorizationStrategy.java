package pl.zimi.flashcards.strategy;

import java.time.Instant;


public interface MemorizationStrategy {
    public Instant nextAfter(Integer numberOfSuccesses, Instant now);

}
