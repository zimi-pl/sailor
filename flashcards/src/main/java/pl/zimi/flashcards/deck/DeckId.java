package pl.zimi.flashcards.deck;

import lombok.Value;
import pl.zimi.repository.annotation.Queryable;

@Value
@Queryable
public class DeckId {

    private String value;

}
