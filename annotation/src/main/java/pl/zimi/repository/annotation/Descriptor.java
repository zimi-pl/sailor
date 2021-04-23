package pl.zimi.repository.annotation;

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
}
