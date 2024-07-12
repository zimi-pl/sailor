package pl.zimi.flashcards.flashcard;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Answer {

    String flashcardId;
    String translation;
    Confidence confidence;

}
