package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;

import java.util.Comparator;

public class Sort {

    private final Descriptor path;
    private final Direction direction;

    public Sort(Descriptor path, final Direction direction) {
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
        return getPath() + " " + direction;
    }

    public int compare(Object o1, Object o2) {
        final Value v1 = Manipulator.get(o1, path.getPath());
        final Value v2 = Manipulator.get(o2, path.getPath());
        return direction.getOrder() * Comparator.<Comparable>naturalOrder().compare(v1, v2);
    }
}
