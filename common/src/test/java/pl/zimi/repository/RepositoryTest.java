package pl.zimi.repository;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.ContractVerificator;
import pl.zimi.repository.contract.MemoryPort;
import pl.zimi.repository.example.Foo;
import pl.zimi.repository.example.SFoo;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class RepositoryTest {

    @TestFactory
    List<DynamicTest> repositoryTest() {
        final var contract = Contract.repository(Foo.class).id(SFoo.foo.id).version(SFoo.foo.version).sortingFeature(true).regexFeature(true).offsetFeature(true);
        return ContractVerificator.test(contract, MemoryPort::port)
                .stream()
                .map(t -> dynamicTest("ContractVerificator." + t.name, () -> t.runnable.run()))
                .collect(Collectors.toList());
    }


}
