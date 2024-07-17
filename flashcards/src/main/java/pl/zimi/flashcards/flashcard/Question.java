package pl.zimi.flashcards.flashcard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.zimi.repository.annotation.Queryable;

@Data
@Builder
@AllArgsConstructor
@Queryable
public class Question {

    String flashcardId;
    Phrase original;

}
