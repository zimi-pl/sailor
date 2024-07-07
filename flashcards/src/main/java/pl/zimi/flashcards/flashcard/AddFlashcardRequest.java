package pl.zimi.flashcards.flashcard;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class AddFlashcardRequest {

    @NonNull
    UserId userId;
    @NonNull
    String word;
    @NonNull
    String translation;

}
