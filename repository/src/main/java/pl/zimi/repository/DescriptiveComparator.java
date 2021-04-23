package pl.zimi.repository;

import ann.Descriptor;

import java.util.Comparator;

public class DescriptiveComparator implements Comparator {

    private final Descriptor path;
    private final Direction direction;

    public DescriptiveComparator(Descriptor path, final Direction direction) {
        this.path = path;
        this.direction = direction;
    }

    public String getPath() {
        return path.getPath();
    }

    public Direction getDirection() {
        return direction;
    }

    public String describe() {
        return getPath() + " " + Direction.NATURAL;
    }

    @Override
    public int compare(Object o1, Object o2) {
        final Comparable v1 = (Comparable)Manipulator.get(o1, path.getPath());
        final Comparable v2 = (Comparable)Manipulator.get(o2, path.getPath());
        return direction.getOrder() * Comparator.<Comparable>naturalOrder().compare(v1, v2);
    }
}
