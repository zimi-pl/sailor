package pl.zimi.repository.contract;

import pl.zimi.repository.manipulation.MemoryRepository;
import pl.zimi.repository.proxy.ContractException;
import pl.zimi.repository.proxy.ProxyProvider;
import pl.zimi.repository.query.Repository;

import java.lang.reflect.Field;

public class MemoryPort {

    public static <T> Repository<T> port(final Contract<T> contract) {
        return new MemoryRepository<T>(contract);
    }

    public static <T> T port(final Class<T> repositoryClass) {
        try {
            final Field contractField = repositoryClass.getField("CONTRACT");
            final Contract contract = (Contract)contractField.get(repositoryClass);
            Repository repository = port(contract);
            return ProxyProvider.provide(repositoryClass, repository);
        } catch (Exception e) {
            throw new ContractException(e);
        }
    }

}
