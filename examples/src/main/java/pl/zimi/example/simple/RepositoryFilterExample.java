package pl.zimi.example.simple;

import pl.zimi.example.simple.clean.InitialData;
import pl.zimi.example.simple.clean.SStudent;
import pl.zimi.example.simple.clean.Student;
import pl.zimi.example.simple.clean.StudentRepository;
import pl.zimi.repository.query.Filter;
import pl.zimi.repository.query.Filters;
import pl.zimi.repository.query.Queries;
import pl.zimi.repository.query.Repository;
import pl.zimi.repository.contract.MemoryPort;

import java.time.Instant;

public class RepositoryFilterExample {

    public static void main(String[] args) {
        Repository<Student> studentRepository = MemoryPort.port(StudentRepository.class);

        InitialData.init(studentRepository);

        Filter filter = Filters.eq(SStudent.student.firstName, "John");

        final var students = studentRepository.find(Queries.filter(filter));

        System.out.println(students);
    }

}
