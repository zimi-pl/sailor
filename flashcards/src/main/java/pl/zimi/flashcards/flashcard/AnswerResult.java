package pl.zimi.flashcards.flashcard;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Value
public class AnswerResult {

    Boolean correct;
    boolean failure;
    List<String> errors;

    public static AnswerResult failure(String error) {
        return new AnswerResult(null, true, Arrays.asList(error));
    }

    public static AnswerResult failure(List<String> errors) {
        return new AnswerResult(null, true, errors);
    }

    public static AnswerResult mistake() {
        return new AnswerResult(false, false, null);
    }

    public static AnswerResult correct() {
        return new AnswerResult(true, false, null);
    }
}
