package pl.zimi.example.simple.clean;

import pl.zimi.repository.query.Filter;
import pl.zimi.repository.query.Filters;
import pl.zimi.repository.query.Queries;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> listAdults() {
        Instant adultBirth = ZonedDateTime.now(ZoneOffset.UTC).minusYears(18).toInstant();
        Filter filter = Filters.lt(SStudent.student.dateOfBirth, adultBirth);
        return studentRepository.find(Queries.filter(filter));
    }

}
