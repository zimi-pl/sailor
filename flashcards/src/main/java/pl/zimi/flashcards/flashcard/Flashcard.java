package pl.zimi.flashcards.flashcard;

import lombok.Builder;
import lombok.Data;
import pl.zimi.flashcards.deck.DeckId;
import pl.zimi.flashcards.user.UserId;
import pl.zimi.repository.annotation.Queryable;

@Data
@Builder
@Queryable
public class Flashcard {
    String id;
    UserId userId;
    Phrase original;
    Phrase translation;
    DeckId deckId;
    MemorizationLevel memorizationLevel;
}
