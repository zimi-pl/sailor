package pl.zimi.repository;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import pl.zimi.repository.contract.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class RepositoryTest {

    @TestFactory
    List<DynamicTest> repositoryTest() {
        final var contract = Contract.repository(Foo.class).sequence(SFoo.foo.seq);
        final var repository = MemoryPort.port(contract);
        return ContractVerificator.test(contract, MemoryPort::port)
                .stream()
                .map(t -> dynamicTest("ContractVerificator." + t.name, () -> t.runnable.run()))
                .collect(Collectors.toList());
    }


}
