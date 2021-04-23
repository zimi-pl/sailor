package pl.zimi.repository.contract;

import pl.zimi.repository.MemoryRepository;
import pl.zimi.repository.Repository;

public class MemoryPort {
    public static <T> Repository<T> port(final Contract<T> contract) {
        return new MemoryRepository<T>(contract);
    }
}
