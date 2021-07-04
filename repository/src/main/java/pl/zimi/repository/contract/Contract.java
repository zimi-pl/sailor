package pl.zimi.repository.contract;

import pl.zimi.repository.annotation.Descriptor;

import java.util.ArrayList;
import java.util.List;

public class Contract<T> {

    private final Class<T> entityClass;
    private final List<Descriptor> sequences = new ArrayList<>();

    public Contract(final Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public static <U> Contract<U> repository(final Class<U> entityClass) {
        return new Contract<U>(entityClass);
    }

    public Contract<T> sequence(final Descriptor value) {
        sequences.add(value);
        return this;
    }

    public List<Descriptor> getSequences() {
        return sequences;
    }

    Class<T> getEntityClass() {
        return entityClass;
    }
}
