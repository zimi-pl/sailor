package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;

public class Comparators {

    public static DescriptiveComparator asc(final Descriptor descriptor) {
        return new DescriptiveComparator(descriptor, Direction.NATURAL);
    }

    public static DescriptiveComparator desc(final Descriptor descriptor) {
        return new DescriptiveComparator(descriptor, Direction.REVERSE);
    }
}
