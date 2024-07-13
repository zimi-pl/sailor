package pl.zimi.repository.annotation;

public class TypedDescriptor<T> extends Descriptor {

    private Class<T> type;

    public TypedDescriptor(final Descriptor parent, final String path, final Class<T> type) {
        super(parent, path);
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }
}
