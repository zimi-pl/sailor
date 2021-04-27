package pl.zimi.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.zimi.repository.*;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.MemoryPort;

import java.util.List;

public class RepositoryTest {

    final Repository<Foo> repository = MemoryPort.port(Contract.repository(Foo.class));

    @Test
    void saveAndRead() {
        final Foo foo = Foo.builder().def("bar").build();
        repository.save(foo);

        Assertions.assertEquals(1, repository.findAll().size());
        Assertions.assertEquals("bar", repository.findAll().get(0).getDef());
    }

    @Test
    void independenceAfterSave() {
        final Foo foo = Foo.builder().def("bar").build();
        repository.save(foo);

        foo.setDef("foo");

        Assertions.assertEquals("bar", repository.findAll().get(0).getDef());
    }

    @Test
    void independenceAfterFind() {
        final Foo foo = Foo.builder().def("bar").build();
        repository.save(foo);

        final Foo first = repository.findAll().get(0);
        first.setDef("changed");

        Assertions.assertEquals("bar", repository.findAll().get(0).getDef());
    }

    @Test
    void filter() {
        final Foo foo = Foo.builder().def("bar").build();
        repository.save(foo);

        final Foo foo2 = Foo.builder().def("other").build();
        repository.save(foo2);

        Assertions.assertEquals(2, repository.findAll().size());
        Assertions.assertEquals(1, repository.find(Predicates.eq(SFoo.foo.def, "bar"), null, null).size());

    }

    @Test
    void noFilter() {
        final Foo foo = Foo.builder().def("bar").build();
        repository.save(foo);

        final Foo foo2 = Foo.builder().def("other").build();
        repository.save(foo2);

        Assertions.assertEquals(2, repository.findAll().size());
        Assertions.assertEquals(2, repository.find(null, null, null).size());

    }

    @Test
    void sort() {
        final Foo foo = Foo.builder().def("bar").build();
        repository.save(foo);

        final Foo foo2 = Foo.builder().def("abc").build();
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.asc(SFoo.foo.def), null);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("abc", list.get(0).getDef());
        Assertions.assertEquals("bar", list.get(1).getDef());

    }

    @Test
    void sortReversed() {
        final Foo foo = Foo.builder().def("bar").build();
        repository.save(foo);

        final Foo foo2 = Foo.builder().def("abc").build();
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.desc(SFoo.foo.def), null);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("bar", list.get(0).getDef());
        Assertions.assertEquals("abc", list.get(1).getDef());

    }

    @Test
    void limit() {
        final Foo foo = Foo.builder().def("bar").build();
        repository.save(foo);

        final Foo foo2 = Foo.builder().def("abc").build();
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.asc(SFoo.foo.def), new LimitOffset(1L, null));
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("abc", list.get(0).getDef());
    }

    @Test
    void offset() {
        final Foo foo = Foo.builder().def("bar").build();
        repository.save(foo);

        final Foo foo2 = Foo.builder().def("abc").build();
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.asc(SFoo.foo.def), new LimitOffset(null, 1L));
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("bar", list.get(0).getDef());
    }

    @Test
    void andPredicate() {
        final Foo foo1 = Foo.builder().def("bar").abc("abc").build();
        repository.save(foo1);

        final Foo foo2 = Foo.builder().def("bar").abc("xyz").build();
        repository.save(foo2);

        final Foo foo3 = Foo.builder().def("xyz").abc("abc").build();
        repository.save(foo3);

        final DescriptivePredicate predicate = Predicates.and(Predicates.eq(SFoo.foo.def, "bar"), Predicates.eq(SFoo.foo.abc, "abc"));
        final List<Foo> foos = repository.find(predicate, null, null);

        Assertions.assertEquals(1, foos.size());
        Assertions.assertEquals("bar", foos.get(0).getDef());
        Assertions.assertEquals("abc", foos.get(0).getAbc());
        Assertions.assertEquals("(def EQUAL bar) AND (abc EQUAL abc)", predicate.describe());

        Assertions.assertEquals(3, repository.find(null, null, null).size());
    }

    @Test
    void orPredicate() {
        final Foo foo1 = Foo.builder().def("bar1").abc("abc").build();
        repository.save(foo1);

        final Foo foo2 = Foo.builder().def("bar2").abc("abc").build();
        repository.save(foo2);

        final Foo foo3 = Foo.builder().def("bar3").abc("abc").build();
        repository.save(foo3);

        final DescriptivePredicate predicate = Predicates.or(Predicates.eq(SFoo.foo.def, "bar1"), Predicates.eq(SFoo.foo.def, "bar2"));
        final List<Foo> foos = repository.find(predicate, Comparators.asc(SFoo.foo.def), null);

        Assertions.assertEquals(2, foos.size());
        Assertions.assertEquals("bar1", foos.get(0).getDef());
        Assertions.assertEquals("bar2", foos.get(1).getDef());
        Assertions.assertEquals("(def EQUAL bar1) OR (def EQUAL bar2)", predicate.describe());

        Assertions.assertEquals(3, repository.find(null, null, null).size());
    }

    @Test
    void lowerThan() {
        final Foo foo1 = Foo.builder().abc("abc").value(7).build();
        repository.save(foo1);

        final Foo foo2 = Foo.builder().abc("abc").value(10).build();
        repository.save(foo2);

        final DescriptivePredicate predicate = Predicates.lt(SFoo.foo.value, 10);
        final List<Foo> foos = repository.find(predicate, null, null);

        Assertions.assertEquals(1, foos.size());
        Assertions.assertEquals(foo1.getValue(), foos.get(0).getValue());
        Assertions.assertEquals("value LOWER_THAN 10", predicate.describe());
    }

    @Test
    void greaterThan() {
        final Foo foo1 = Foo.builder().abc("abc").value(7).build();
        repository.save(foo1);

        final Foo foo2 = Foo.builder().abc("abc").value(10).build();
        repository.save(foo2);

        final DescriptivePredicate predicate = Predicates.gt(SFoo.foo.value, 7);
        final List<Foo> foos = repository.find(predicate, null, null);

        Assertions.assertEquals(1, foos.size());
        Assertions.assertEquals(foo2.getValue(), foos.get(0).getValue());
        Assertions.assertEquals("value GREATER_THAN 7", predicate.describe());
    }

    @Test
    void compoundObject() {
        final Foo foo1 = Foo.builder()
                .bar(Bar.builder().str("some text").build())
                .value(7).build();
        repository.save(foo1);

        final DescriptivePredicate predicate = Predicates.eq(SFoo.foo.bar.str, "some text");
        final var foos = repository.find(predicate, null, null);

        Assertions.assertEquals(1, foos.size());
        Assertions.assertEquals("bar.str EQUAL some text", predicate.describe());
    }

    @Test
    void compoundObjectNullHandling() {
        final Foo foo1 = Foo.builder()
                .value(7).build();
        repository.save(foo1);

        final DescriptivePredicate predicate = Predicates.eq(SFoo.foo.bar.str, "some text");
        final var foos = repository.find(predicate, null, null);

        Assertions.assertEquals(0, foos.size());
        Assertions.assertEquals("bar.str EQUAL some text", predicate.describe());
    }

    @Test
    void nullHandlingLowerThan() {
        repository.save(Foo.builder().abc("siema").build());

        final DescriptivePredicate predicate = Predicates.lt(SFoo.foo.value, 5);
        final var foos = repository.find(predicate, null, null);

        Assertions.assertEquals(0, foos.size());
        Assertions.assertEquals("value LOWER_THAN 5", predicate.describe());
    }

    @Test
    void nullHandlingGreaterThan() {
        repository.save(Foo.builder().abc("siema").build());

        final DescriptivePredicate predicate = Predicates.gt(SFoo.foo.value, 5);
        final var foos = repository.find(predicate, null, null);

        Assertions.assertEquals(0, foos.size());
        Assertions.assertEquals("value GREATER_THAN 5", predicate.describe());
    }

    @Test
    void sortingAscendingWithNull() {
        repository.save(Foo.builder().abc("siema").bar(Bar.builder().str("abc").build()).build());
        repository.save(Foo.builder().abc("siema").bar(null).build());

        final var comparator = new DescriptiveComparator(SFoo.foo.bar.str, Direction.NATURAL);
        final var foos = repository.find(null, comparator, null);
        Assertions.assertEquals(2, foos.size());
        Assertions.assertEquals(null, foos.get(0).getBar());
        Assertions.assertEquals("abc", foos.get(1).getBar().getStr());
        Assertions.assertEquals("bar.str NATURAL", comparator.describe());
    }

    @Test
    void sortingDescendingWithNull() {
        repository.save(Foo.builder().abc("siema").bar(null).build());
        repository.save(Foo.builder().abc("siema").bar(Bar.builder().str("abc").build()).build());

        final var comparator = new DescriptiveComparator(SFoo.foo.bar.str, Direction.REVERSE);
        final var foos = repository.find(null, comparator, null);
        Assertions.assertEquals(2, foos.size());
        Assertions.assertEquals("abc", foos.get(0).getBar().getStr());
        Assertions.assertEquals(null, foos.get(1).getBar());
        Assertions.assertEquals("bar.str REVERSE", comparator.describe());
    }

}
