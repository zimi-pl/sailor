package pl.zimi.example.simple;

import pl.zimi.repository.Filter;
import pl.zimi.repository.Filters;
import pl.zimi.repository.Queries;
import pl.zimi.repository.Repository;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.MemoryPort;

import java.time.Instant;

public class StudentMain {

    public static void main(String[] args) {
        Repository<Student> studentRepository = MemoryPort.port(new Contract<>(Student.class));

        studentRepository.save(new Student("1", "John", "Doe", Instant.parse("2000-01-01T15:00:00Z")));
        studentRepository.save(new Student("2", "Jane", "Smith", Instant.parse("2003-01-01T15:00:00Z")));


        Filter filter = Filters.eq(SStudent.student.firstName, "John");

        final var students = studentRepository.find(Queries.filter(filter));

        System.out.println(students);
    }

}
