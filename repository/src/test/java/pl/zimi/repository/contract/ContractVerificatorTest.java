package pl.zimi.repository.contract;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ContractVerificatorTest {

    private Contract<Foo> contract = Contract.repository(Foo.class).id(SFoo.foo.id).version(SFoo.foo.version);

    @Test
    void saveAndRead() {
        ContractVerificator.saveAndRead(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void independenceAfterSave() {
        ContractVerificator.independenceAfterSave(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void independenceAfterFind() {
        ContractVerificator.independenceAfterFind(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void filterStringEqual() {
        ContractVerificator.filterStringEqual(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void filterStringRegex() {
        ContractVerificator.filterStringRegex(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void noFilter() {
        ContractVerificator.noFilter(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void sort() {
        ContractVerificator.sort(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void sortReversed() {
        ContractVerificator.sortReversed(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void limit() {
        ContractVerificator.limit(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void offset() {
        ContractVerificator.offset(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void andPredicate() {
        ContractVerificator.andPredicate(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void orPredicate() {
        ContractVerificator.orPredicate(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void lowerThan() {
        ContractVerificator.lowerThan(MemoryPort.port(contract), Foo.class, SFoo.foo.def, SFoo.foo.value);
    }
    
    @Test
    void greaterThan() {
        ContractVerificator.greaterThan(MemoryPort.port(contract), Foo.class, SFoo.foo.def, SFoo.foo.value);
    }

    @Test
    void compoundObject() {
        ContractVerificator.compoundObject(MemoryPort.port(contract), Foo.class, SFoo.foo.bar.str);
    }

    @Test
    void compoundObjectNullHandling() {
        ContractVerificator.compoundObjectNullHandling(MemoryPort.port(contract), Foo.class, SFoo.foo.def, SFoo.foo.bar.str);
    }

    @Test
    void nullHandlingLowerThan() {
        ContractVerificator.nullHandlingLowerThan(MemoryPort.port(contract), Foo.class, SFoo.foo.def, SFoo.foo.value);
    }

    @Test
    void nullHandlingGreaterThan() {
        ContractVerificator.nullHandlingGreaterThan(MemoryPort.port(contract), Foo.class, SFoo.foo.def, SFoo.foo.value);
    }

    @Test
    void sortingAscendingWithNull() {
        ContractVerificator.sortingAscendingWithNull(MemoryPort.port(contract), Foo.class, SFoo.foo.bar.str, SFoo.foo.def);
    }

    @Test
    void sortingDescendingWithNull() {
        ContractVerificator.sortingDescendingWithNull(MemoryPort.port(contract), Foo.class, SFoo.foo.bar.str, SFoo.foo.def);
    }

    @Test
    void idStringContract() {
        ContractVerificator.idStringContract(MemoryPort.port(contract), Foo.class, SFoo.foo.id);
    }

    @Test
    void idStringContractNextValue() {
        ContractVerificator.idStringContractNextValue(MemoryPort.port(contract), Foo.class, SFoo.foo.id);
    }

    @Test
    void versionContract() {
        ContractVerificator.versionContract(MemoryPort.port(contract), Foo.class, SFoo.foo.version);
    }

    @Test
    void versionContractNextValue() {
        ContractVerificator.versionContractNextValue(MemoryPort.port(contract), Foo.class, SFoo.foo.version);
    }

    @Test
    void versionContractOptimisticLock() {
        ContractVerificator.versionContractOptimisticLock(MemoryPort.port(contract), Foo.class, SFoo.foo.id, SFoo.foo.version);
    }
    @Test
    void delete() {
        ContractVerificator.delete(MemoryPort.port(contract), Foo.class, SFoo.foo.def);
    }

    @Test
    void missingIdContract() {
        final var contract = Contract.repository(Foo.class);
        ContractVerificator.missingIdContract(MemoryPort.port(contract), Foo.class);
    }

    @Test
    void existingIdContract() {
        final var contract = Contract.repository(Foo.class).id(SFoo.foo.id);
        ContractVerificator.existingIdContract(MemoryPort.port(contract), Foo.class);
    }
}