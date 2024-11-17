package pl.zimi.flashcards.deck;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.zimi.repository.annotation.Queryable;

@Queryable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckId {

    private String value;

}
