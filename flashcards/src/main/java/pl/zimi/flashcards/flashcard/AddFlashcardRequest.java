package pl.zimi.flashcards.flashcard;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import pl.zimi.flashcards.user.UserId;

@Value
@Builder
public class AddFlashcardRequest {

    @NonNull
    UserId userId;
    @NonNull
    Word original;
    @NonNull
    Word translation;

}
