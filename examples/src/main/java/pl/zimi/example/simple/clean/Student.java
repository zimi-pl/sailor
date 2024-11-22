package pl.zimi.example.simple.clean;

import pl.zimi.repository.annotation.Queryable;

import java.time.Instant;

@Queryable
public class Student {

    private String id;
    private String firstName;
    private String lastName;
    private Instant dateOfBirth;

    public Student(String id, String firstName, String lastName, Instant dateOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }
}
