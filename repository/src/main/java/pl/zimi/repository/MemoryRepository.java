package pl.zimi.repository;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoryRepository<T> implements Repository<T> {

    private final Gson gson = new Gson();
    private final List<T> source = new ArrayList<>();

    private T deepCopy(final T toCopy) {
        final Class<T> type = (Class<T>) toCopy.getClass();
        return gson.fromJson(gson.toJson(toCopy), type);
    }

    @Override
    public T save(T entity) {
        final T copied = deepCopy(entity);
        source.add(copied);
        return copied;
    }

    @Override
    public List<T> find(final DescriptivePredicate predicate, final DescriptiveComparator comparator, final LimitOffset limit) {
        final Stream<T> streamed = source.stream();
        final Stream<T> filtered = predicate != null ? (Stream<T>)streamed.filter(predicate) : streamed;
        final Stream<T> sorted = comparator != null ? (Stream<T>)filtered.sorted(comparator) : filtered;
        final Stream<T> skipped = limit != null && limit.getOffset() != null ? sorted.skip(limit.getOffset()) : sorted;
        final Stream<T> limited = limit != null && limit.getLimit() != null ? skipped.limit(limit.getLimit()) : skipped;
        return limited
                .map(this::deepCopy)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findAll() {
        return find(null, null, null);
    }

    @Override
    public List<T> deleteAll(Predicate<T> predicate) {
        return null;
    }


}
