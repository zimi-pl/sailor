package pl.zimi.flashcards.user;

import lombok.Value;

@Value(staticConstructor = "of")
public class UserId {
    String value;
}
