package pl.zimi.repository.query;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    T save(final T entity);

    Optional<T> findById(Object id);

    List<T> find(final Query query);

    T delete(T entity);

}
