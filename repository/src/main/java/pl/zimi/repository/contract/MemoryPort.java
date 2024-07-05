package pl.zimi.repository.contract;

import pl.zimi.repository.manipulation.MemoryRepository;
import pl.zimi.repository.query.Repository;

public class MemoryPort {
    public static <T> Repository<T> port(final Contract<T> contract) {
        return new MemoryRepository<T>(contract);
    }
}
