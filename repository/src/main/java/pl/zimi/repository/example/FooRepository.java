package pl.zimi.repository.example;

import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.query.Repository;

public interface FooRepository extends Repository<Foo> {

    Contract<Foo> CONTRACT = Contract.repository(Foo.class).id(SFoo.foo.id);

}
