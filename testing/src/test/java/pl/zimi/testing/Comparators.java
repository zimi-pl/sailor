package pl.zimi.testing;

import pl.zimi.repository.annotation.Descriptor;
import pl.zimi.repository.DescriptiveComparator;
import pl.zimi.repository.Direction;

public class Comparators {

    public static DescriptiveComparator asc(final Descriptor descriptor) {
        return new DescriptiveComparator(descriptor, Direction.NATURAL);
    }

    public static DescriptiveComparator desc(final Descriptor descriptor) {
        return new DescriptiveComparator(descriptor, Direction.REVERSE);
    }
}
