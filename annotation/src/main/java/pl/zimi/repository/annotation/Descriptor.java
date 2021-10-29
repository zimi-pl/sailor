package pl.zimi.repository.annotation;

import java.util.Objects;

public class Descriptor {

    private final Descriptor parent;
    private final String path;

    public Descriptor(final Descriptor parent, final String path) {
        this.parent = parent;
        this.path = path;
    }

    public String getPath() {
        final String prefix = parent != null ? (parent.getPath().isEmpty() ? "" : parent.getPath() + ".") : "";
        return prefix + path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Descriptor that = (Descriptor) o;
        return Objects.equals(parent, that.parent) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, path);
    }

    public Descriptor getParent() {
        return parent;
    }
}
