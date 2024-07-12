package pl.zimi.flashcards.flashcard;

import lombok.Builder;
import lombok.Data;
import pl.zimi.repository.annotation.Queryable;

@Data
@Builder
@Queryable
public class Word {
    private String text;
    private String context;
}
