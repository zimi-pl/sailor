package pl.zimi.flashcards.flashcard;

import lombok.Value;

@Value(staticConstructor = "of")
public class UserId {
    String value;
}
