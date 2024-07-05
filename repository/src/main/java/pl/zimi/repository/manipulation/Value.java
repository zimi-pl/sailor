package pl.zimi.repository.manipulation;

public class Value implements Comparable {

    private final Object object;
    private final String failureReason;

    public Value(Object object, String failureReason) {
        this.object = object;
        this.failureReason = failureReason;
    }

    public static Value value(final Object value) {
        return new Value(value, null);
    }

    public static Value failure(String failureReason) {
        return new Value(null, failureReason);
    }

    public Object getObject() {
        return object;
    }

    public String getFailureReason() {
        return failureReason;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Value)) {
            throw new IllegalArgumentException("object is not Value class");
        }
        Value v = (Value)o;
        if (this.object == null && v.object == null) {
            return 0;
        }
        if (this.object == null && v.object != null) {
            return -1;
        }
        if (this.object != null && v.object == null) {
            return 1;
        }
        return ((Comparable)this.object).compareTo(v.object);
    }
}
