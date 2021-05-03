package pl.zimi.repository.contract;

import pl.zimi.repository.Comparators;
import pl.zimi.repository.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContractVerificator {

    public static void assertThese(final Contract<Foo> contract, final Function<Contract<Foo>, Repository<Foo>> supplier) {
        for (final Test single : test(contract, supplier)) {
            single.runnable.run();
        }
    }

    public static List<Test> test(final Contract<Foo> contract, final Function<Contract<Foo>, Repository<Foo>> supplier) {
        final var repository = supplier.apply(contract);
        final var sequenceRepository = supplier.apply(contract);
        final var list = Arrays.asList(
                new Test("saveAndRead", () -> saveAndRead(contract, repository)),
                new Test("independenceAfterSave", () -> independenceAfterSave(contract, repository)),
                new Test("independenceAfterFind", () -> independenceAfterFind(contract, repository)),
                new Test("filter", () -> filter(contract, repository)),
                new Test("noFilter", () -> noFilter(contract, repository)),
                new Test("sort", () -> sort(contract, repository)),
                new Test("sortReversed", () -> sortReversed(contract, repository)),
                new Test("limit", () -> limit(contract, repository)),
                new Test("offset", () -> offset(contract, repository)),
                new Test("andPredicate", () -> andPredicate(contract, repository)),
                new Test("orPredicate", () -> orPredicate(contract, repository)),
                new Test("lowerThan", () -> lowerThan(contract, repository)),
                new Test("greaterThan", () -> greaterThan(contract, repository)),
                new Test("compoundObject", () -> compoundObject(contract, repository)),
                new Test("compoundObjectNullHandling", () -> compoundObjectNullHandling(contract, repository)),
                new Test("nullHandlingLowerThan", () -> nullHandlingLowerThan(contract, repository)),
                new Test("nullHandlingGreaterThan", () -> nullHandlingGreaterThan(contract, repository)),
                new Test("sortingAscendingWithNull", () -> sortingAscendingWithNull(contract, repository)),
                new Test("sortingDescendingWithNull", () -> sortingDescendingWithNull(contract, repository))
        );
        final var tests = new ArrayList<>(list);

        if (!contract.getSequences().isEmpty()) {
            tests.add(new Test("sequenceContract", () -> sequenceContract(contract, sequenceRepository)));
            tests.add(new Test("sequenceContractFollowing", () -> sequenceContractFollowing(contract, repository)));
        }
        Collections.shuffle(tests);
        return tests;
    }

    public static class Test {
        public String name;
        public Runnable runnable;

        Test(final String name, final Runnable runnable) {
            this.name = name;
            this.runnable = runnable;
        }
    }

    static void saveAndRead(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var first = "001_saveAndRead";
        final Foo foo = Foo.builder().def(first).build();
        repository.save(foo);

        final var all = repository.findAll();
        final var collect = all.stream().filter(f -> first.equals(f.getDef())).collect(Collectors.toList());
        assertEquals(1, collect.size());
        assertEquals(first, collect.get(0).getDef());
    }

    static void independenceAfterSave(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var first = "001_independenceAfterSave";
        final Foo foo = Foo.builder().def(first).build();
        repository.save(foo);

        final var second = "002_independenceAfterSave";
        foo.setDef(second);

        assertEquals(1, repository.find(Predicates.eq(SFoo.foo.def, first), null, null).size());
        assertEquals(0, repository.find(Predicates.eq(SFoo.foo.def, second), null, null).size());
    }

    static void independenceAfterFind(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var first = "001_independenceAfterFind";
        final Foo foo = Foo.builder().def(first).build();
        repository.save(foo);

        final Foo fooFound = repository.find(Predicates.eq(SFoo.foo.def, first), null, null).get(0);
        final var second = "002_independenceAfterFind";
        fooFound.setDef(second);

        final var predicate = Predicates.or(Predicates.eq(SFoo.foo.def, first), Predicates.eq(SFoo.foo.def, second));
        assertEquals(first, repository.find(predicate, null, null).get(0).getDef());
    }

    static void filter(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var first = "001_filter";
        final Foo foo = Foo.builder().def(first).build();
        repository.save(foo);

        final var second = "002_filter";
        final Foo foo2 = Foo.builder().def(second).build();
        repository.save(foo2);

        assertEquals(2L, repository.findAll().stream()
                .filter(f -> Arrays.asList(first, second).contains(f.getDef()))
                .count());
        assertEquals(1, repository.find(Predicates.eq(SFoo.foo.def, first), null, null).size());

    }

    static void noFilter(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var first = "001_noFilter";
        final Foo foo = Foo.builder().def(first).build();
        repository.save(foo);

        final var second = "002_noFilter";
        final Foo foo2 = Foo.builder().def(second).build();
        repository.save(foo2);

        assertEquals(2L, repository.findAll().stream()
                .filter(f -> Arrays.asList(first, second).contains(f.getDef()))
                .count());
        assertEquals(2L, repository.find(null, null, null).stream()
                .filter(f -> Arrays.asList(first, second).contains(f.getDef()))
                .count());

    }

    static void sort(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var first = "002_sort";
        final Foo foo = Foo.builder().def(first).build();
        repository.save(foo);

        final var second = "001_sort";
        final Foo foo2 = Foo.builder().def(second).build();
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.asc(SFoo.foo.def), null);
        final var collected = list.stream().filter(f -> Arrays.asList(first, second).contains(f.getDef())).collect(Collectors.toList());
        assertEquals(2, collected.size());
        assertEquals(second, collected.get(0).getDef());
        assertEquals(first, collected.get(1).getDef());

    }

    static void sortReversed(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var first = "002_sortReversed";
        final Foo foo = Foo.builder().def(first).build();
        repository.save(foo);

        final var second = "001_sortReversed";
        final Foo foo2 = Foo.builder().def(second).build();
        repository.save(foo2);

        final List<Foo> list = repository.find(null, Comparators.desc(SFoo.foo.def), null);
        final var collected = list.stream().filter(f -> Arrays.asList(first, second).contains(f.getDef())).collect(Collectors.toList());
        assertEquals(2, collected.size());
        assertEquals(first, collected.get(0).getDef());
        assertEquals(second, collected.get(1).getDef());

    }

    static void limit(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var second = "002_limit";
        final Foo foo = Foo.builder().def(second).build();
        repository.save(foo);

        final var first = "001_limit";
        final Foo foo2 = Foo.builder().def(first).build();
        repository.save(foo2);

        final var a = Predicates.eq(SFoo.foo.def, second);
        final var b = Predicates.eq(SFoo.foo.def, first);
        final List<Foo> list = repository.find(Predicates.or(a, b), Comparators.asc(SFoo.foo.def), new LimitOffset(1L, null));
        assertEquals(1, list.size());
        assertEquals(first, list.get(0).getDef());
    }

    static void offset(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var second = "002_offset";
        final Foo foo = Foo.builder().def(second).build();
        repository.save(foo);

        final var first = "001_offset";
        final Foo foo2 = Foo.builder().def(first).build();
        repository.save(foo2);

        var predicate = Predicates.or(Predicates.eq(SFoo.foo.def, first), Predicates.eq(SFoo.foo.def, second));
        final List<Foo> list = repository.find(predicate, Comparators.asc(SFoo.foo.def), new LimitOffset(null, 1L));
        assertEquals(1, list.size());
        assertEquals(second, list.get(0).getDef());
    }

    static void andPredicate(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var andPredicatePart1 = "001_andPredicate";
        final var andPredicatePart2 = "002_andPredicate";
        final var other = "other_andPredicate";

        final Foo foo1 = Foo.builder().def(andPredicatePart1).abc(andPredicatePart2).build();
        repository.save(foo1);

        final Foo foo2 = Foo.builder().def(andPredicatePart1).abc(other).build();
        repository.save(foo2);

        final Foo foo3 = Foo.builder().def(other).abc(andPredicatePart2).build();
        repository.save(foo3);

        final DescriptivePredicate predicate = Predicates.and(Predicates.eq(SFoo.foo.def, andPredicatePart1), Predicates.eq(SFoo.foo.abc, andPredicatePart2));
        final List<Foo> foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals(andPredicatePart1, foos.get(0).getDef());
        assertEquals(andPredicatePart2, foos.get(0).getAbc());
        assertEquals("(def EQUAL 001_andPredicate) AND (abc EQUAL 002_andPredicate)", predicate.describe());

        assertEquals(2, repository.find(Predicates.eq(SFoo.foo.def, andPredicatePart1), null, null).size());
        assertEquals(2, repository.find(Predicates.eq(SFoo.foo.abc, andPredicatePart2), null, null).size());
    }

    static void orPredicate(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var first = "001_orPredicate";
        final var orPredicate = "orPredicate";
        final Foo foo1 = Foo.builder().def(first).abc(orPredicate).build();
        repository.save(foo1);

        final var second = "002_orPredicate";
        final Foo foo2 = Foo.builder().def(second).abc(orPredicate).build();
        repository.save(foo2);

        final var third = "003_orPredicate";
        final Foo foo3 = Foo.builder().def(third).abc(orPredicate).build();
        repository.save(foo3);

        final DescriptivePredicate predicate = Predicates.or(Predicates.eq(SFoo.foo.def, first), Predicates.eq(SFoo.foo.def, second));
        final List<Foo> foos = repository.find(predicate, Comparators.asc(SFoo.foo.def), null);

        assertEquals(2, foos.size());
        assertEquals(first, foos.get(0).getDef());
        assertEquals(second, foos.get(1).getDef());
        assertEquals("(def EQUAL 001_orPredicate) OR (def EQUAL 002_orPredicate)", predicate.describe());

        assertEquals(3, repository.find(Predicates.eq(SFoo.foo.abc, orPredicate), null, null).size());
    }

    static void lowerThan(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var lowerThan = "lowerThan";
        final Foo foo1 = Foo.builder().abc(lowerThan).value(7).build();
        repository.save(foo1);

        final Foo foo2 = Foo.builder().abc(lowerThan).value(10).build();
        repository.save(foo2);

        final DescriptivePredicate predicateLt = Predicates.lt(SFoo.foo.value, 10);
        final var predicate = Predicates.and(Predicates.eq(SFoo.foo.abc, lowerThan), predicateLt);
        final List<Foo> foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals(foo1.getValue(), foos.get(0).getValue());
        assertEquals("value LOWER_THAN 10", predicateLt.describe());
    }

    static void greaterThan(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var greaterThan = "greaterThan";
        final Foo foo1 = Foo.builder().abc(greaterThan).value(7).build();
        repository.save(foo1);

        final Foo foo2 = Foo.builder().abc(greaterThan).value(10).build();
        repository.save(foo2);

        final DescriptivePredicate predicateGt = Predicates.gt(SFoo.foo.value, 7);
        final var predicate = Predicates.and(Predicates.eq(SFoo.foo.abc, greaterThan), Predicates.gt(SFoo.foo.value, 7));

        final List<Foo> foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals(foo2.getValue(), foos.get(0).getValue());
        assertEquals("value GREATER_THAN 7", predicateGt.describe());
    }

    static void compoundObject(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var compoundObject = "compoundObject";
        final Foo foo1 = Foo.builder()
                .bar(Bar.builder().str(compoundObject).build())
                .value(7).build();
        repository.save(foo1);

        final DescriptivePredicate predicate = Predicates.eq(SFoo.foo.bar.str, compoundObject);
        final var foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals("bar.str EQUAL compoundObject", predicate.describe());
    }

    static void compoundObjectNullHandling(final Contract<Foo> contract, final Repository<Foo> repository) {
        final Foo foo1 = Foo.builder()
                .value(7).build();
        repository.save(foo1);

        final var compoundObjectNullHandling = "compoundObjectNullHandling";
        final DescriptivePredicate predicate = Predicates.eq(SFoo.foo.bar.str, compoundObjectNullHandling);
        final var foos = repository.find(predicate, null, null);

        assertEquals(0, foos.size());
        assertEquals("bar.str EQUAL compoundObjectNullHandling", predicate.describe());
    }

    static void nullHandlingLowerThan(final Contract<Foo> contract, final Repository<Foo> repository) {
        repository.save(Foo.builder().abc("siema").build());

        final DescriptivePredicate predicate = Predicates.lt(SFoo.foo.value, 5);
        final var foos = repository.find(predicate, null, null);

        assertEquals(0, foos.size());
        assertEquals("value LOWER_THAN 5", predicate.describe());
    }

    static void nullHandlingGreaterThan(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var nullHandlingGreaterThan = "nullHandlingGreaterThan";
        repository.save(Foo.builder().abc(nullHandlingGreaterThan).build());

        final var predicateGt = Predicates.gt(SFoo.foo.value, 5);
        final var predicate = Predicates.and(Predicates.eq(SFoo.foo.abc, nullHandlingGreaterThan), predicateGt);
        final var foos = repository.find(predicate, null, null);

        assertEquals(0, foos.size());
        assertEquals("value GREATER_THAN 5", predicateGt.describe());
    }

    static void sortingAscendingWithNull(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var sortingAscendingWithNull = "sortingAscendingWithNull";
        final var first = "001_sortingAscendingWithNull";
        repository.save(Foo.builder().abc(sortingAscendingWithNull).bar(Bar.builder().str(first).build()).build());
        repository.save(Foo.builder().abc(sortingAscendingWithNull).bar(null).build());

        final var comparator = new DescriptiveComparator(SFoo.foo.bar.str, Direction.NATURAL);
        final var predicate = Predicates.eq(SFoo.foo.abc, sortingAscendingWithNull);
        final var foos = repository.find(predicate, comparator, null);
        assertEquals(2, foos.size());
        assertEquals(null, foos.get(0).getBar());
        assertEquals(first, foos.get(1).getBar().getStr());
        assertEquals("bar.str NATURAL", comparator.describe());
    }

    static void sortingDescendingWithNull(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var sortingDescendingWithNull = "sortingDescendingWithNull";
        repository.save(Foo.builder().abc(sortingDescendingWithNull).bar(null).build());
        final var first = "001_sortingDescendingWithNull";
        repository.save(Foo.builder().abc(sortingDescendingWithNull).bar(Bar.builder().str(first).build()).build());

        final var comparator = new DescriptiveComparator(SFoo.foo.bar.str, Direction.REVERSE);
        final var predicate = Predicates.eq(SFoo.foo.abc, sortingDescendingWithNull);
        final var foos = repository.find(predicate, comparator, null);
        assertEquals(2, foos.size());
        assertEquals(first, foos.get(0).getBar().getStr());
        assertEquals(null, foos.get(1).getBar());
        assertEquals("bar.str REVERSE", comparator.describe());
    }

    static void sequenceContract(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var saved = repository.save(Foo.builder().abc("abc").build());
        assertEquals(1, saved.getSeq());
    }

    static void sequenceContractFollowing(final Contract<Foo> contract, final Repository<Foo> repository) {
        final var first = repository.save(Foo.builder().abc("abc").build());

        final var another = repository.save(Foo.builder().abc("abc").build());
        assertTrue(first.getSeq() < another.getSeq());
    }

    private static void assertTrue(final boolean test) {
        if (!test) {
            throw new RuntimeException("Condition not satisfied");
        }
    }

    private static void assertEquals(final Object expected, final Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new RuntimeException("Values are not equals, expected: " + expected + ", actual: " + actual);
        }
    }

}
