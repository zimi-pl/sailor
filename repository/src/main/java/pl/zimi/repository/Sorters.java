package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;

public class Sorters {

    public static Sorter asc(final Descriptor descriptor) {
        return new Sorter(descriptor, Direction.NATURAL);
    }

    public static Sorter desc(final Descriptor descriptor) {
        return new Sorter(descriptor, Direction.REVERSE);
    }
}
