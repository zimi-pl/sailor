package pl.zimi.repository.proxy;

import org.junit.jupiter.api.Test;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.MemoryPort;
import pl.zimi.repository.example.Foo;
import pl.zimi.repository.example.FooRepository;
import pl.zimi.repository.example.SFoo;
import pl.zimi.repository.query.Repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProxyProviderTest {

    @Test
    void shouldProvideProxy() {
        final Contract<Foo> contract = Contract.repository(Foo.class).id(SFoo.foo.id);
        final Repository<Foo> port = MemoryPort.port(contract);

        final FooRepository fooRepository = ProxyProvider.provide(FooRepository.class, port);

        final Foo saved = fooRepository.save(Foo.builder().abc("abc").def("def").build());
        assertNotNull(saved.getId());
    }

}