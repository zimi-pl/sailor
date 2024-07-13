package pl.zimi.flashcards.deck;

import lombok.Builder;
import lombok.Data;
import pl.zimi.repository.annotation.Queryable;

@Data
@Builder
@Queryable
public class Deck {

    DeckId id;

    String name;


}
