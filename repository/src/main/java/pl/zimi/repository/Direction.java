package pl.zimi.repository;

public enum Direction {
    NATURAL(1), REVERSE(-1);

    private int order;

    Direction(final int order) {
        this.order = order;
    }

    int getOrder() {
        return order;
    }

}
