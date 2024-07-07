package pl.zimi.example.simple;

import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.query.Repository;

public interface StudentRepository extends Repository<Student> {

    Contract<Student> CONTRACT = Contract.repository(Student.class);
}
