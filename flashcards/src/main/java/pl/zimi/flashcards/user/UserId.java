package pl.zimi.flashcards.user;

import lombok.Value;
import pl.zimi.repository.annotation.Queryable;

@Value(staticConstructor = "of")
@Queryable
public class UserId {
    String value;
}
