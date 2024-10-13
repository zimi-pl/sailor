package pl.zimi.example.simple.clean;

import lombok.RequiredArgsConstructor;
import pl.zimi.repository.query.Filter;
import pl.zimi.repository.query.Filters;
import pl.zimi.repository.query.Queries;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> listAdults() {
        Instant adultBirth = ZonedDateTime.now(ZoneOffset.UTC).minusYears(18).toInstant();
        Filter filter = Filters.lt(SStudent.student.dateOfBirth, adultBirth);
        return studentRepository.find(Queries.filter(filter));
    }

}
