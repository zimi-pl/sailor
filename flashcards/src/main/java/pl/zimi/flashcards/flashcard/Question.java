package pl.zimi.flashcards.flashcard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.zimi.repository.annotation.Queryable;

@Data
@Builder
@AllArgsConstructor
@Queryable
@NoArgsConstructor
public class Question {

    String flashcardId;
    Phrase original;
    Phrase translation;

}
