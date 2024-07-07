package pl.zimi.flashcards.flashcard;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import pl.zimi.flashcards.strategy.MemorizationStrategy;
import pl.zimi.repository.annotation.Queryable;

import java.time.Instant;

@Builder
@EqualsAndHashCode
@Queryable
@Data
public class MemorizationLevel {

    Integer numberOfSuccesses;

    Integer numberOfAnswers;

    Instant useAfter;

    static MemorizationLevel none() {
        return MemorizationLevel.builder().numberOfSuccesses(0).numberOfAnswers(0).build();
    }

    static MemorizationLevel level(int numberOfSuccesses) {
        return MemorizationLevel.builder().numberOfSuccesses(numberOfSuccesses).build();
    }

    public void upgrade(Instant now, MemorizationStrategy memorizationStrategy) {
        this.useAfter = memorizationStrategy.nextAfter(numberOfSuccesses, now);
        this.numberOfSuccesses++;
        this.numberOfAnswers++;
    }

    public void downgrade(Instant now, MemorizationStrategy memorizationStrategy) {
        this.numberOfSuccesses = 0;
        this.useAfter = memorizationStrategy.nextAfter(numberOfSuccesses, now);
        this.numberOfAnswers++;
    }
}
