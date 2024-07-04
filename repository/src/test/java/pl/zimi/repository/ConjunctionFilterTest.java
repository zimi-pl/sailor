package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConjunctionFilterTest {

    @Test
    void unknownOperator() {
        final var eqBar1 = Filters.eq(new Descriptor(null, "a"), "bar1");
        final var eqBar2 = Filters.eq(new Descriptor(null, "a"), "bar2");
        assertThrows(IllegalArgumentException.class, () -> new ConjunctionFilter("TEST", eqBar1, eqBar2));
    }

}

