package pl.zimi.flashcards.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.zimi.repository.annotation.Queryable;

@Queryable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserId {
    String value;

    public static UserId of(String value) {
        return new UserId(value);
    }
}
