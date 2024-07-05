package pl.zimi.repository.manipulation;

import com.google.gson.Gson;
import pl.zimi.repository.annotation.Descriptor;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.OptimisticLockException;
import pl.zimi.repository.query.Filters;
import pl.zimi.repository.query.Queries;
import pl.zimi.repository.query.Query;
import pl.zimi.repository.query.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoryRepository<T> implements Repository<T> {

    private final Gson gson = new Gson();
    private final Map<Object, T> source = new HashMap<>();
    private final Contract<T> contract;
    private final AtomicInteger idCounter = new AtomicInteger(0);
    private final Descriptor versionDescriptor;

    public MemoryRepository(final Contract<T> contract) {
        this.contract = contract;
        this.versionDescriptor = contract.getVersion();
    }

    private T deepCopy(final T toCopy) {
        final Class<T> type = (Class<T>) toCopy.getClass();
        return gson.fromJson(gson.toJson(toCopy), type);
    }

    @Override
    public T save(T entity) {
        final T copied = deepCopy(entity);
        if (contract.getId() != null && Manipulator.get(copied, contract.getId()).getObject() == null) {
            Manipulator.set(copied, contract.getId(), Integer.toString(idCounter.getAndIncrement()));
            if (versionDescriptor != null) {
                Manipulator.set(copied, versionDescriptor, 0);
            }
        } else if (contract.getId() != null && versionDescriptor != null) {
            final var previousVersion = Manipulator.get(entity, versionDescriptor).getObject();
            final var id = Manipulator.get(entity, contract.getId()).getObject();
            final var currentEntity = source.get(id);
            if (currentEntity != null) {
                final var dbVersion = Manipulator.get(currentEntity, versionDescriptor).getObject();
                if (Objects.equals(previousVersion, dbVersion)) {
                    Manipulator.set(copied, versionDescriptor, ((Integer) previousVersion) + 1);
                } else {
                    throw new OptimisticLockException("Given version: " + previousVersion + ", db version: " + dbVersion);
                }
            } else {
                throw new OptimisticLockException("Given version: " + previousVersion + ", db version: null");
            }
        }
        final var id = contract.getId() != null ? Manipulator.get(copied, contract.getId()).getObject() : UUID.randomUUID().toString();
        source.put(id, copied);
        return deepCopy(copied);
    }

    @Override
    public Optional<T> findById(Object id) {
        if (contract.getId() == null) {
            throw new UnsupportedOperationException();
        }
        final var list = find(Queries.filter(Filters.eq(contract.getId(), id)));
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    @Override
    public List<T> find(final Query query) {
        final var filter = query.getFilter();
        final var sort = query.getSorter();
        final var limit = query.getLimitOffset();
        final Stream<T> streamed = source.values().stream();
        final Stream<T> filtered = filter != null ? streamed.filter(filter::test) : streamed;
        final Stream<T> sorted = sort != null ? filtered.sorted(sort::compare) : filtered;
        final Stream<T> skipped = limit != null && limit.getOffset() != null ? sorted.skip(limit.getOffset()) : sorted;
        final Stream<T> limited = limit != null && limit.getLimit() != null ? skipped.limit(limit.getLimit()) : skipped;
        return limited
                .map(this::deepCopy)
                .collect(Collectors.toList());
    }

    public T delete(T entity) {
        Object id = Manipulator.get(entity, contract.getId()).getObject();
        source.remove(id);
        return entity;
    }

}
