package pl.zimi.repository;

import java.util.List;
import java.util.function.Predicate;

public interface Repository<T> {

    T save(final T entity);

    List<T> find(final DescriptivePredicate predicate, final DescriptiveComparator comparator, final LimitOffset limit);

    List<T> findAll();

    List<T> deleteAll(final Predicate<T> predicate);

}
