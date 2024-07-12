package pl.zimi.flashcards.flashcard;

import lombok.Builder;
import lombok.Data;
import pl.zimi.flashcards.user.UserId;
import pl.zimi.repository.annotation.Queryable;

@Data
@Builder
@Queryable
public class Flashcard {
    String id;
    UserId userId;
    Word original;
    Word translation;
    MemorizationLevel memorizationLevel;
}
