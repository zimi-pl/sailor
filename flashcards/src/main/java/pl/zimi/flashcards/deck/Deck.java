package pl.zimi.flashcards.deck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.zimi.repository.annotation.Queryable;

@Data
@Builder
@Queryable
@NoArgsConstructor
@AllArgsConstructor
public class Deck {

    DeckId id;

    String name;


}
