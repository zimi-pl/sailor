package pl.zimi.flashcards.user;

import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.query.Repository;

public interface StudentRepository extends Repository<Student> {

    Contract<Student> CONTRACT = Contract.repository(Student.class);

}
