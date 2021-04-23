package pl.zimi.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.zimi.repository.*;

public class PredicatesTest {

    @Test
    void equal() {
        final DescriptivePredicate predicate = Predicates.eq(SFoo.foo.abc, "value");

        final Foo abc = new Foo();
        abc.setAbc("value");
        Assertions.assertTrue(predicate.test(abc));

        Assertions.assertEquals("abc EQUAL value", predicate.describe());
    }

    @Test
    void equalWithNegativeResult() {
        final DescriptivePredicate predicate = Predicates.eq(SFoo.foo.abc, "value");

        final Foo foo = new Foo();
        foo.setAbc("other");
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
