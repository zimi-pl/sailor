package pl.zimi.repository.contract;

import pl.zimi.repository.annotation.Descriptor;

public class Contract<T> {

    private final Class<T> entityClass;
    private Descriptor version;
    private Descriptor id;

    private boolean sortingFeature;
    private boolean regexFeature;
    private boolean offsetFeature;

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

    public Contract<T> sortingFeature(final boolean sorting) {
        this.sortingFeature = sorting;
        return this;
    }

    public Contract<T> offsetFeature(final boolean offsetFeature) {
        this.offsetFeature = offsetFeature;
        return this;
    }

    public Contract<T> regexFeature(final boolean regexFeature) {
        this.regexFeature = regexFeature;
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

    public boolean isSortingFeature() {
        return sortingFeature;
    }

    public boolean isRegexFeature() {
        return regexFeature;
    }

    public boolean isOffsetFeature() {
        return offsetFeature;
    }
}
