package pl.zimi.repository;

public class LimitOffset {

    private Long limit;
    private Long offset;

    public LimitOffset(Long limit, Long offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public Long getLimit() {
        return limit;
    }

    public Long getOffset() {
        return offset;
    }
}
