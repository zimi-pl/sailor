package pl.zimi.repository.contract;

import org.junit.jupiter.api.Test;
import pl.zimi.repository.example.BarRepository;
import pl.zimi.repository.example.FooRepository;
import pl.zimi.repository.proxy.ContractException;

import static org.junit.jupiter.api.Assertions.*;

class MemoryPortTest {

    @Test
    void shouldReturnPort() {
        final var port = MemoryPort.port(FooRepository.class);
        assertNotNull(port);
    }

    @Test
    void shouldThrowExceptionWhenThereIsNoContract() {
        assertThrows(ContractException.class, () -> MemoryPort.port(BarRepository.class));
    }

}