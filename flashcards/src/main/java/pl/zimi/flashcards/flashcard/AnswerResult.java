package pl.zimi.flashcards.flashcard;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class AnswerResult {

    Boolean correct;
    boolean failure;

    public static AnswerResult failure() {
        return new AnswerResult(null, true);
    }

    public static AnswerResult mistake() {
        return new AnswerResult(false, false);
    }

    public static AnswerResult correct() {
        return new AnswerResult(true, false);
    }
}
