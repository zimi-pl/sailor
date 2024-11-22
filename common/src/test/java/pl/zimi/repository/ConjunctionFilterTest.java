package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;
import org.junit.jupiter.api.Test;
import pl.zimi.repository.query.ConjunctionFilter;
import pl.zimi.repository.query.Filter;
import pl.zimi.repository.query.Filters;

import static org.junit.jupiter.api.Assertions.*;

class ConjunctionFilterTest {

    @Test
    void unknownOperator() {
        final Filter eqBar1 = Filters.eq(new Descriptor(null, "a"), "bar1");
        final Filter eqBar2 = Filters.eq(new Descriptor(null, "a"), "bar2");
        assertThrows(IllegalArgumentException.class, () -> new ConjunctionFilter("TEST", eqBar1, eqBar2));
    }

}

