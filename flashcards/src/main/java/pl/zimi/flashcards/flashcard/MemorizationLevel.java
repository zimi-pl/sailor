package pl.zimi.flashcards.flashcard;

import lombok.*;
import pl.zimi.flashcards.strategy.MemorizationStrategy;
import pl.zimi.repository.annotation.Queryable;

import java.time.Instant;

@Builder
@EqualsAndHashCode
@Queryable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemorizationLevel {

    Integer numberOfSuccesses;

    Integer numberOfAnswers;

    Instant useAfter;

    public static MemorizationLevel none() {
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
