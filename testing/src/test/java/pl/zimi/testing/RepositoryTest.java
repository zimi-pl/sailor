package pl.zimi.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.zimi.repository.*;

import java.util.List;

public class RepositoryTest {

    final Repository<Foo> repository = RepositoryFactory.newInstance(Foo.class);

    @Test
    void saveAndRead() {
        final Foo foo = new Foo();
        foo.setBar("bar");
        repository.save(foo);

        Assertions.assertEquals(1, repository.findAll().size());
        Assertions.assertEquals("bar", repository.findAll().get(0).getBar());
    }

    @Test
    void independenceAfterSave() {
        final Foo foo = new Foo();
        foo.setBar("bar");
        repository.save(foo);

        foo.setBar("foo");

        Assertions.assertEquals("bar", repository.findAll().get(0).getBar());
    }

    @Test
    void independenceAfterFind() {
        final Foo foo = new Foo();
        foo.setBar("bar");
        repository.save(foo);

        final Foo first = repository.findAll().get(0);
        first.setBar("changed");

        Assertions.assertEquals("bar", repository.findAll().get(0).getBar());
    }

    @Test
    void filter() {
        final Foo foo = new Foo();
        foo.setBar("bar");
        repository.save(foo);

        final Foo foo2 = new Foo();
        foo2.setBar("other");
        repository.save(foo2);

        Assertions.assertEquals(2, repository.findAll().size());
        Assertions.assertEquals(1, repository.find(Predicates.eq(SFoo.foo.bar, "bar"), null, null).size());

    }

    @Test
    void noFilter() {
        final Foo foo = new Foo();
        foo.setBar("bar");
        repository.save(foo);

        final Foo foo2 = new Foo();
        foo2.setBar("other");
        repository.save(foo2);

        Assertions.assertEquals(2, repository.findAll().size());
        Assertions.assertEquals(2, repository.find(null, null, null).size());

    }

    @Test
    void sort() {
        final Foo foo = new Foo();
        foo.setBar("bar");
        repository.save(foo);

        final Foo foo2 = new Foo();
        foo2.setBar("abc");
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.asc(SFoo.foo.bar), null);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("abc", list.get(0).getBar());
        Assertions.assertEquals("bar", list.get(1).getBar());

    }

    @Test
    void sortReversed() {
        final Foo foo = new Foo();
        foo.setBar("bar");
        repository.save(foo);

        final Foo foo2 = new Foo();
        foo2.setBar("abc");
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.desc(SFoo.foo.bar), null);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("bar", list.get(0).getBar());
        Assertions.assertEquals("abc", list.get(1).getBar());

    }

    @Test
    void limit() {
        final Foo foo = new Foo();
        foo.setBar("bar");
        repository.save(foo);

        final Foo foo2 = new Foo();
        foo2.setBar("abc");
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.asc(SFoo.foo.bar), new LimitOffset(1L, null));
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("abc", list.get(0).getBar());
    }

    @Test
    void offset() {
        final Foo foo = new Foo();
        foo.setBar("bar");
        repository.save(foo);

        final Foo foo2 = new Foo();
        foo2.setBar("abc");
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.asc(SFoo.foo.bar), new LimitOffset(null, 1L));
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("bar", list.get(0).getBar());
    }

    @Test
    void andPredicate() {
        final Foo foo1 = new Foo();
        foo1.setBar("bar");
        foo1.setAbc("abc");
        repository.save(foo1);

        final Foo foo2 = new Foo();
        foo2.setBar("bar");
        foo2.setAbc("xyz");
        repository.save(foo2);

        final Foo foo3 = new Foo();
        foo3.setBar("xyz");
        foo3.setAbc("abc");
        repository.save(foo3);

        final DescriptivePredicate predicate = Predicates.and(Predicates.eq(SFoo.foo.bar, "bar"), Predicates.eq(SFoo.foo.abc, "abc"));
        final List<Foo> foos = repository.find(predicate, null, null);

        Assertions.assertEquals(1, foos.size());
        Assertions.assertEquals("bar", foos.get(0).getBar());
        Assertions.assertEquals("abc", foos.get(0).getAbc());
        Assertions.assertEquals("(bar EQUAL bar) AND (abc EQUAL abc)", predicate.describe());

        Assertions.assertEquals(3, repository.find(null, null, null).size());
    }

    @Test
    void orPredicate() {
        final Foo foo1 = new Foo();
        foo1.setBar("bar1");
        foo1.setAbc("abc");
        repository.save(foo1);

        final Foo foo2 = new Foo();
        foo2.setBar("bar2");
        foo2.setAbc("abc");
        repository.save(foo2);

        final Foo foo3 = new Foo();
        foo3.setBar("bar3");
        foo3.setAbc("abc");
        repository.save(foo3);

        final DescriptivePredicate predicate = Predicates.or(Predicates.eq(SFoo.foo.bar, "bar1"), Predicates.eq(SFoo.foo.bar, "bar2"));
        final List<Foo> foos = repository.find(predicate, Comparators.asc(SFoo.foo.bar), null);

        Assertions.assertEquals(2, foos.size());
        Assertions.assertEquals("bar1", foos.get(0).getBar());
        Assertions.assertEquals("bar2", foos.get(1).getBar());
        Assertions.assertEquals("(bar EQUAL bar1) OR (bar EQUAL bar2)", predicate.describe());

        Assertions.assertEquals(3, repository.find(null, null, null).size());
    }

    @Test
    void lowerThan() {
        Assertions.fail();
    }
}
