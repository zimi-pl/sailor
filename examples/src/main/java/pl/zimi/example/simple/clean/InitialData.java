package pl.zimi.example.simple.clean;

import pl.zimi.repository.query.Repository;

import java.time.Instant;

public class InitialData {
    public static void init(Repository<Student> studentRepository) {
        studentRepository.save(new Student("1", "John", "Doe", Instant.parse("2000-01-01T15:00:00Z")));
        studentRepository.save(new Student("2", "Jane", "Smith", Instant.parse("2003-01-01T15:00:00Z")));
        studentRepository.save(new Student("2", "Carol", "Spider", Instant.parse("2010-01-01T15:00:00Z")));
    }
}
