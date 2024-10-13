package pl.zimi.example.simple.clean;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import pl.zimi.repository.annotation.Queryable;

import java.time.Instant;

@Data
@Builder
@Queryable
public class Student {

    private String id;
    private String firstName;
    private String lastName;
    private Instant dateOfBirth;
}
