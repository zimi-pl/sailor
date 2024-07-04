package pl.zimi.flashcards.flashcard;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import pl.zimi.repository.annotation.Queryable;

@Builder
@EqualsAndHashCode
@Queryable
public class MemorizationLevel {

    Integer numberOfSuccesses;

    static MemorizationLevel none() {
        return MemorizationLevel.builder().numberOfSuccesses(0).build();
    }

    static MemorizationLevel level(int numberOfSuccesses) {
        return MemorizationLevel.builder().numberOfSuccesses(numberOfSuccesses).build();
    }

    public void upgrade() {
        this.numberOfSuccesses++;
    }
}
