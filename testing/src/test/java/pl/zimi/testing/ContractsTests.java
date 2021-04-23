package pl.zimi.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.zimi.repository.Repository;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.MemoryPort;

public class ContractsTests {

    @Test
    void sequenceContract() {
        final Contract<Foo> contract = Contract.repository(Foo.class)
                .sequence(SFoo.foo.value);

        final Repository<Foo> repository = MemoryPort.port(contract);

        final var saved = repository.save(Foo.builder().abc("abc").build());
        Assertions.assertEquals(1, saved.getValue());
    }

    @Test
    void sequenceContractFollowing() {
        final Contract<Foo> contract = Contract.repository(Foo.class)
                .sequence(SFoo.foo.value);

        final Repository<Foo> repository = MemoryPort.port(contract);
        repository.save(Foo.builder().abc("abc").build());

        final var another = repository.save(Foo.builder().abc("abc").build());
        Assertions.assertEquals(2, another.getValue());
    }
}
