package pl.zimi.repository.query;

public class LimitOffset {

    private Long limit;
    private Long offset;

    private LimitOffset(Long limit, Long offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public static LimitOffset limitOffset(Long limit, Long offset) {
        return new LimitOffset(limit, offset);
    }

    public static LimitOffset limit(Long limit) {
        return new LimitOffset(limit, null);
    }

    public Long getLimit() {
        return limit;
    }

    public Long getOffset() {
        return offset;
    }
}
