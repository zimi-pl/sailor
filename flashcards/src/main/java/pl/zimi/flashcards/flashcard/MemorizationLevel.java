package pl.zimi.flashcards.flashcard;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.zimi.flashcards.strategy.MemorizationStrategy;
import pl.zimi.repository.annotation.Queryable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Builder
@EqualsAndHashCode
@Queryable
@Data
public class MemorizationLevel {

    Integer numberOfSuccesses;

    Instant useAfter;

    static MemorizationLevel none() {
        return MemorizationLevel.builder().numberOfSuccesses(0).build();
    }

    static MemorizationLevel level(int numberOfSuccesses) {
        return MemorizationLevel.builder().numberOfSuccesses(numberOfSuccesses).build();
    }

    public void upgrade(Instant now, MemorizationStrategy memorizationStrategy) {
        this.useAfter = memorizationStrategy.nextAfter(numberOfSuccesses, now);
        this.numberOfSuccesses++;
    }
}
