package pl.zimi.example.simple.clean;

import pl.zimi.repository.query.Repository;

import java.time.Instant;

public class InitialData {
    public static void init(Repository<Student> studentRepository) {
        studentRepository.save(Student.builder().id("1").firstName("John").lastName("Doe").dateOfBirth(Instant.parse("2000-01-01T15:00:00Z")).build());
        studentRepository.save(Student.builder().id("2").firstName("Jane").lastName("Smith").dateOfBirth(Instant.parse("2003-01-01T15:00:00Z")).build());
        studentRepository.save(Student.builder().id("2").firstName("Carol").lastName("Spider").dateOfBirth(Instant.parse("2010-01-01T15:00:00Z")).build());
    }
}
