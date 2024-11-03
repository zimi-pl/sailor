package pl.zimi.repository.contract;

import pl.zimi.repository.annotation.Descriptor;

public class Contract<T> {

    private final Class<T> entityClass;
    private Descriptor version;
    private Descriptor id;

    private boolean sorting;

    public Contract(final Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public static <U> Contract<U> repository(final Class<U> entityClass) {
        return new Contract<>(entityClass);
    }

    public Contract<T> version(final Descriptor value) {
        version = value;
        return this;
    }

    public Contract<T> id(final Descriptor value) {
        id = value;
        return this;
    }

    public Contract<T> sorting(final boolean sorting) {
        this.sorting = sorting;
        return this;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Descriptor getVersion() {
        return version;
    }

    public Descriptor getId() {
        return id;
    }

    public boolean isSorting() {
        return sorting;
    }
}
