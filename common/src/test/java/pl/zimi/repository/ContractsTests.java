package pl.zimi.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.ContractVerificator;
import pl.zimi.repository.contract.MemoryPort;
import pl.zimi.repository.example.Foo;
import pl.zimi.repository.example.SFoo;
import pl.zimi.repository.query.Repository;

import java.util.function.Function;

public class ContractsTests {

    @Test
    void testSupersetContract() {
        final Contract<Foo> contract = Contract.repository(Foo.class)
                .version(SFoo.foo.version);

        final Contract<Foo> contractForPort = Contract.repository(Foo.class);
        final Function<Contract<Foo>, Repository<Foo>> differentSupplier = c -> MemoryPort.port(contractForPort);
        Assertions.assertThrows(RuntimeException.class, () -> ContractVerificator.assertThese(contract, differentSupplier));
    }

    @Test
    void testSubsetContract() {
        final Contract<Foo> contract = Contract.repository(Foo.class).sortingFeature(true).offsetFeature(true).regexFeature(true);
        final Contract<Foo> contractForPort = Contract.repository(Foo.class)
                .version(SFoo.foo.version)
                .sortingFeature(true);

        final Function<Contract<Foo>, Repository<Foo>> differentSupplier = c -> MemoryPort.port(contractForPort);
        ContractVerificator.assertThese(contract, differentSupplier);
    }

    @Test
    void testExactContract() {
        final Contract<Foo> contract = Contract.repository(Foo.class).sortingFeature(true).offsetFeature(true).regexFeature(true);

        final Function<Contract<Foo>, Repository<Foo>> differentSupplier = c -> MemoryPort.port(contract);
        ContractVerificator.assertThese(contract, differentSupplier);
    }

}
