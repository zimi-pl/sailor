package pl.zimi.flashcards.user;

import lombok.Builder;
import lombok.Value;
import pl.zimi.repository.annotation.Queryable;

import java.time.Instant;

@Value
@Builder
@Queryable
public class Student {

    String id;
    String firstName;
    String lastName;
    Instant dateOfBirth;
}
