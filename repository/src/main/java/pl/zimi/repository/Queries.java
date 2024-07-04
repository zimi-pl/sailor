package pl.zimi.repository;

public class Queries {
    public static Query filter(final Filter filter) {
        return new Query(filter, null, null);
    }

    public static Query query(Filter filter, Sort sort, LimitOffset limitOffset) {
        return new Query(filter, sort, limitOffset);
    }

    public static Query all() {
        return new Query(null, null, null);
    }
}
