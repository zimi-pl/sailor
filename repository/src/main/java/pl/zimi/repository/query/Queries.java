package pl.zimi.repository.query;

public class Queries {
    public static Query filter(final Filter filter) {
        return new Query(filter, null, null);
    }

    public static Query query(Filter filter, Sorter sorter, LimitOffset limitOffset) {
        return new Query(filter, sorter, limitOffset);
    }

    public static Query all() {
        return new Query(null, null, null);
    }
}
