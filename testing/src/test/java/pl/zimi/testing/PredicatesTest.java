package pl.zimi.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.zimi.repository.*;
import pl.zimi.repository.contract.Foo;
import pl.zimi.repository.contract.SFoo;

public class PredicatesTest {

    @Test
    void equal() {
        final DescriptivePredicate predicate = Predicates.eq(SFoo.foo.abc, "value");

        final Foo abc = Foo.builder().abc("value").build();
        Assertions.assertTrue(predicate.test(abc));

        Assertions.assertEquals("abc EQUAL value", predicate.describe());
    }

    @Test
    void equalWithNegativeResult() {
        final DescriptivePredicate predicate = Predicates.eq(SFoo.foo.abc, "value");

        final Foo foo = Foo.builder().abc("other").build();
        Assertions.assertFalse(predicate.test(foo));
        Assertions.assertEquals("abc EQUAL value", predicate.describe());
    }

    @Test
    void descriptiveComparator() {
        final DescriptiveComparator asc = Comparators.asc(SFoo.foo.abc);
        Assertions.assertEquals("abc", asc.getPath());
        Assertions.assertEquals(Direction.NATURAL, asc.getDirection());
        Assertions.assertEquals("abc NATURAL", asc.describe());
    }

}
