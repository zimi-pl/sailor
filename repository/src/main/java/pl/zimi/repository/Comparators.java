package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;

public class Comparators {

    public static Sort asc(final Descriptor descriptor) {
        return new Sort(descriptor, Direction.NATURAL);
    }

    public static Sort desc(final Descriptor descriptor) {
        return new Sort(descriptor, Direction.REVERSE);
    }
}
