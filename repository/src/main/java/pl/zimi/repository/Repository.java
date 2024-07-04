package pl.zimi.repository;

import java.util.List;
import java.util.function.Predicate;

public interface Repository<T> {

    T save(final T entity);

    List<T> find(final Filter predicate, final Sort comparator, final LimitOffset limit);

    List<T> findAll();

    T delete(T entity);

    List<T> deleteAll(final Predicate<T> predicate);

}
