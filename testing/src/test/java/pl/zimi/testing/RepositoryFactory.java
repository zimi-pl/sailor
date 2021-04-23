package pl.zimi.testing;

import pl.zimi.repository.MemoryRepository;
import pl.zimi.repository.Repository;

public class RepositoryFactory {
    public static  <T> Repository<T> newInstance(final Class<T> type) {
        return new MemoryRepository<T>();
    }
}
