package pl.zimi.repository.query;

public class Query {

    Filter filter;

    Sorter sorter;

    LimitOffset limitOffset;

    public Query(Filter filter, Sorter sorter, LimitOffset limitOffset) {
        this.filter = filter;
        this.sorter = sorter;
        this.limitOffset = limitOffset;
    }

    public Filter getFilter() {
        return filter;
    }

    public Sorter getSorter() {
        return sorter;
    }

    public LimitOffset getLimitOffset() {
        return limitOffset;
    }

    public static class QueryBuilder {
        private Filter filter;

        private Sorter sorter;

        private LimitOffset limitOffset;

        public QueryBuilder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public QueryBuilder sorter(Sorter sorter) {
            this.sorter = sorter;
            return this;
        }

        public QueryBuilder limitOffset(LimitOffset limitOffset) {
            this.limitOffset = limitOffset;
            return this;
        }

        public Query build() {
            return new Query(filter, sorter, limitOffset);
        }
    }

    public static QueryBuilder builder() {
        return new QueryBuilder();
    }
}
