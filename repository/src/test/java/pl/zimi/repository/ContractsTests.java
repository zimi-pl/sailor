package pl.zimi.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.zimi.repository.Repository;
import pl.zimi.repository.contract.*;

import java.util.function.Function;

public class ContractsTests {

    @Test
    void testSupersetContract() {
        final Contract<Foo> contract = Contract.repository(Foo.class)
                .sequence(SFoo.foo.seq);

        final var contractForPort = Contract.repository(Foo.class);
        final Function<Contract<Foo>, Repository<Foo>> differentSupplier = c -> MemoryPort.port(contractForPort);
        final var ex = Assertions.assertThrows(RuntimeException.class, () -> {
            ContractVerificator.assertThese(contract, differentSupplier);
        });
    }

    @Test
    void testSubsetContract() {
        final Contract<Foo> contract = Contract.repository(Foo.class);
        final var contractForPort = Contract.repository(Foo.class)
                .sequence(SFoo.foo.seq);

        final Function<Contract<Foo>, Repository<Foo>> differentSupplier = c -> MemoryPort.port(contractForPort);
        ContractVerificator.assertThese(contract, differentSupplier);
    }

    @Test
    void testExactContract() {
        final Contract<Foo> contract = Contract.repository(Foo.class);

        final Function<Contract<Foo>, Repository<Foo>> differentSupplier = c -> MemoryPort.port(contract);
        ContractVerificator.assertThese(contract, differentSupplier);
    }

}
