package pl.zimi.repository.manipulation;

import com.google.gson.Gson;
import pl.zimi.repository.annotation.Descriptor;
import pl.zimi.repository.annotation.TypedDescriptor;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.OptimisticLockException;
import pl.zimi.repository.contract.UnsupportedFeatureException;
import pl.zimi.repository.query.*;

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

    @Override
    public T save(T entity) {
        final T copied = Manipulator.deepCopy(entity);
        if (contract.getId() != null && Manipulator.get(copied, contract.getId()).getObject() == null) {
            final String newId = Integer.toString(idCounter.getAndIncrement());
            if (contract.getId() instanceof TypedDescriptor && !((TypedDescriptor)contract.getId()).getType().equals(String.class)) {
                final Object id;
                try {
                    id = ((TypedDescriptor) contract.getId()).getType().getConstructor(String.class).newInstance(newId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                Manipulator.set(copied, contract.getId(), id);
            } else {
                Manipulator.set(copied, contract.getId(), newId);
            }
            if (versionDescriptor != null) {
                Manipulator.set(copied, versionDescriptor, 0);
            }
        } else if (contract.getId() != null && versionDescriptor != null) {
            final Object previousVersion = Manipulator.get(entity, versionDescriptor).getObject();
            final Object id = Manipulator.get(entity, contract.getId()).getObject();
            final T currentEntity = source.get(id);
            if (currentEntity != null) {
                final Object dbVersion = Manipulator.get(currentEntity, versionDescriptor).getObject();
                if (Objects.equals(previousVersion, dbVersion)) {
                    Manipulator.set(copied, versionDescriptor, ((Integer) previousVersion) + 1);
                } else {
                    throw new OptimisticLockException("Given version: " + previousVersion + ", db version: " + dbVersion);
                }
            } else {
                throw new OptimisticLockException("Given version: " + previousVersion + ", db version: null");
            }
        }
        final Object id = contract.getId() != null ? Manipulator.get(copied, contract.getId()).getObject() : UUID.randomUUID().toString();
        source.put(id, copied);
        return Manipulator.deepCopy(copied);
    }

    @Override
    public Optional<T> findById(Object id) {
        if (contract.getId() == null) {
            throw new UnsupportedOperationException();
        }
        final List<T> list = find(Queries.filter(Filters.eq(contract.getId(), id)));
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    @Override
    public List<T> find(final Query query) {
        final Filter filter = query.getFilter();
        final Sorter sort = query.getSorter();
        if (sort != null && !contract.isSortingFeature()) {
            throw new UnsupportedFeatureException("Sorting");
        }
        final LimitOffset limit = query.getLimitOffset();
        final Stream<T> streamed = source.values().stream();
        final Stream<T> filtered = filter != null ? streamed.filter(filter::test) : streamed;
        final Stream<T> sorted = sort != null ? filtered.sorted(sort::compare) : filtered;
        final Stream<T> skipped = limit != null && limit.getOffset() != null ? sorted.skip(limit.getOffset()) : sorted;
        final Stream<T> limited = limit != null && limit.getLimit() != null ? skipped.limit(limit.getLimit()) : skipped;
        return limited
                .map(toCopy -> Manipulator.deepCopy(toCopy))
                .collect(Collectors.toList());
    }

    public T delete(T entity) {
        Object id = Manipulator.get(entity, contract.getId()).getObject();
        source.remove(id);
        return entity;
    }

}
