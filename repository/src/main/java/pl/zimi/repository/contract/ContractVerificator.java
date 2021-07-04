package pl.zimi.repository.contract;

import pl.zimi.repository.Comparators;
import pl.zimi.repository.*;
import pl.zimi.repository.annotation.Descriptor;

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
                new Test("saveAndRead", () -> saveAndRead(repository, contract.getEntityClass(), SFoo.foo.def)),
                new Test("independenceAfterSave", () -> independenceAfterSave(repository, contract.getEntityClass(), SFoo.foo.def)),
                new Test("independenceAfterFind", () -> independenceAfterFind(repository, contract.getEntityClass(), SFoo.foo.def)),
                new Test("noFilter", () -> noFilter(repository, contract.getEntityClass(), SFoo.foo.def)),
                new Test("limit", () -> limit(repository, contract.getEntityClass(), SFoo.foo.def)),
                new Test("offset", () -> offset(repository, contract.getEntityClass(), SFoo.foo.def)),
                new Test("andPredicate", () -> andPredicate(repository, contract.getEntityClass(), SFoo.foo.def, SFoo.foo.abc)),
                new Test("orPredicate", () -> orPredicate(repository, contract.getEntityClass(), SFoo.foo.def)),

                new Test("filterStringEqual", () -> filterStringEqual(repository, contract.getEntityClass(), SFoo.foo.def)),
                new Test("filterStringRegex", () -> filterStringRegex(repository, contract.getEntityClass(), SFoo.foo.def)),
                new Test("sort", () -> sort(repository, contract.getEntityClass(), SFoo.foo.def)),
                new Test("sortReversed", () -> sortReversed(repository, contract.getEntityClass(), SFoo.foo.def)),

                new Test("lowerThan", () -> lowerThan(repository, contract.getEntityClass(), SFoo.foo.def, SFoo.foo.value)),
                new Test("greaterThan", () -> greaterThan(repository, contract.getEntityClass(), SFoo.foo.def, SFoo.foo.value)),

                new Test("compoundObject", () -> compoundObject(repository, contract.getEntityClass(), Bar.class, SFoo.foo.bar, SFoo.foo.bar.str)),
                new Test("compoundObjectNullHandling", () -> compoundObjectNullHandling(repository, contract.getEntityClass(), SFoo.foo.def, SFoo.foo.bar.str)),
                new Test("nullHandlingLowerThan", () -> nullHandlingLowerThan(repository, contract.getEntityClass(), SFoo.foo.def, SFoo.foo.value)),
                new Test("nullHandlingGreaterThan", () -> nullHandlingGreaterThan(repository, contract.getEntityClass(), SFoo.foo.def, SFoo.foo.value)),
                new Test("sortingAscendingWithNull", () -> sortingAscendingWithNull(repository, contract.getEntityClass(), Bar.class, SFoo.foo.bar, SFoo.foo.bar.str,SFoo.foo.def)),
                new Test("sortingDescendingWithNull", () -> sortingDescendingWithNull(repository, contract.getEntityClass(), Bar.class, SFoo.foo.bar, SFoo.foo.bar.str,SFoo.foo.def))
        );
        final var tests = new ArrayList<>(list);

        if (!contract.getSequences().isEmpty()) {
            tests.add(new Test("sequenceContract", () -> sequenceContract(sequenceRepository, contract.getEntityClass(), SFoo.foo.seq)));
            tests.add(new Test("sequenceContractFollowing", () -> sequenceContractFollowing(repository, contract.getEntityClass(), SFoo.foo.seq)));
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

    static <T> void saveAndRead(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_saveAndRead";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), first);

        //change type to other
        repository.save(foo);

        final var all = repository.findAll();
        final var collect = all.stream().filter(f -> first.equals(Manipulator.get(f, descriptor.getPath()).getObject())).collect(Collectors.toList());
        assertEquals(1, collect.size());
        assertEquals(first, Manipulator.get(collect.get(0), descriptor.getPath()).getObject());
    }

    static <T> void independenceAfterSave(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_independenceAfterSave";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), first);
        repository.save(foo);

        final var second = "002_independenceAfterSave";
        Manipulator.set(foo, descriptor.getPath(), second);

        assertEquals(1, repository.find(Predicates.eq(descriptor, first), null, null).size());
        assertEquals(0, repository.find(Predicates.eq(descriptor, second), null, null).size());
    }

    static <T> void independenceAfterFind(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_independenceAfterFind";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), first);
        repository.save(foo);

        final T fooFound = repository.find(Predicates.eq(descriptor, first), null, null).get(0);
        final var second = "002_independenceAfterFind";
        Manipulator.set(fooFound, descriptor.getPath(), second);

        final var predicate = Predicates.or(Predicates.eq(descriptor, first), Predicates.eq(descriptor, second));
        assertEquals(first, Manipulator.get(repository.find(predicate, null, null).get(0), descriptor.getPath()).getObject());
    }

    static <T> void filterStringEqual(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_filterStringEqual";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), first);
        repository.save(foo);

        final var second = "002_filterStringEqual";
        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor.getPath(), second);
        repository.save(foo2);

        assertEquals(2L, repository.findAll().stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.get(f, descriptor.getPath()).getObject()))
                .count());
        assertEquals(1, repository.find(Predicates.eq(descriptor, first), null, null).size());
    }

    static <T> void filterStringRegex(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_filterStringRegex";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), first);
        repository.save(foo);

        final var capitalS = Predicates.and(Predicates.eq(descriptor, first), Predicates.regex(descriptor, "S"));
        assertEquals(1, repository.find(capitalS, null, null).size());

        final var capitalB = Predicates.and(Predicates.eq(descriptor, first), Predicates.regex(descriptor, "B"));
        assertEquals(0, repository.find(capitalB, null, null).size());

        final var startsWith = Predicates.and(Predicates.eq(descriptor, first), Predicates.regex(descriptor, "^000"));
        assertEquals(0, repository.find(startsWith, null, null).size());

        final var startsWithCorrect = Predicates.and(Predicates.eq(descriptor, first), Predicates.regex(descriptor, "^001"));
        assertEquals(1, repository.find(startsWithCorrect, null, null).size());
    }

    static <T> void noFilter(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_noFilter";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), first);
        repository.save(foo);

        final var second = "002_noFilter";
        final T foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor.getPath(), second);
        repository.save(foo2);

        assertEquals(2L, repository.findAll().stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.get(f, descriptor.getPath()).getObject()))
                .count());
        assertEquals(2L, repository.find(null, null, null).stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.get(f, descriptor.getPath()).getObject()))
                .count());

    }

    static <T> void sort(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "002_sort";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), first);
        repository.save(foo);

        final var second = "001_sort";
        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor.getPath(), second);
        repository.save(foo2);

        final List<T> list = repository.find(null, Comparators.asc(descriptor), null);
        final var collected = list.stream().filter(f -> Arrays.asList(first, second).contains(Manipulator.get(f, descriptor.getPath()).getObject())).collect(Collectors.toList());
        assertEquals(2, collected.size());
        assertEquals(second, Manipulator.get(collected.get(0), descriptor.getPath()).getObject());
        assertEquals(first, Manipulator.get(collected.get(1), descriptor.getPath()).getObject());

    }

    static <T> void sortReversed(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "002_sortReversed";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), first);
        repository.save(foo);

        final var second = "001_sortReversed";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor.getPath(), second);
        repository.save(foo1);

        final List<T> list = repository.find(null, Comparators.desc(descriptor), null);
        final var collected = list.stream().filter(f -> Arrays.asList(first, second).contains(Manipulator.get(f, descriptor.getPath()).getObject())).collect(Collectors.toList());
        assertEquals(2, collected.size());
        assertEquals(first, Manipulator.get(collected.get(0), descriptor.getPath()).getObject());
        assertEquals(second, Manipulator.get(collected.get(1), descriptor.getPath()).getObject());

    }

    static <T> void limit(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var second = "002_limit";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), second);
        repository.save(foo);

        final var first = "001_limit";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor.getPath(), first);
        repository.save(foo1);

        final var a = Predicates.eq(descriptor, second);
        final var b = Predicates.eq(descriptor, first);
        final List<T> list = repository.find(Predicates.or(a, b), Comparators.asc(descriptor), new LimitOffset(1L, null));
        assertEquals(1, list.size());
        assertEquals(first, Manipulator.get(list.get(0), descriptor.getPath()).getObject());
    }

    static <T> void offset(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var second = "002_offset";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor.getPath(), second);
        repository.save(foo);

        final var first = "001_offset";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor.getPath(), first);
        repository.save(foo1);

        var predicate = Predicates.or(Predicates.eq(descriptor, first), Predicates.eq(descriptor, second));
        final List<T> list = repository.find(predicate, Comparators.asc(descriptor), new LimitOffset(null, 1L));
        assertEquals(1, list.size());
        assertEquals(second, Manipulator.get(list.get(0), descriptor.getPath()).getObject());
    }

    static <T> void andPredicate(final Repository<T> repository, final Class<T> clazz, final Descriptor firstDescriptor, final Descriptor secondDescriptor) {
        final var andPredicatePart1 = "001_andPredicate";
        final var andPredicatePart2 = "002_andPredicate";
        final var other = "other_andPredicate";

        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, firstDescriptor.getPath(), andPredicatePart1);
        Manipulator.set(foo1, secondDescriptor.getPath(), andPredicatePart2);
        repository.save(foo1);

        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, firstDescriptor.getPath(), andPredicatePart1);
        Manipulator.set(foo2, secondDescriptor.getPath(), other);
        repository.save(foo2);

        final var foo3 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo3, firstDescriptor.getPath(), other);
        Manipulator.set(foo3, secondDescriptor.getPath(), andPredicatePart2);
        repository.save(foo3);

        final DescriptivePredicate predicate = Predicates.and(Predicates.eq(firstDescriptor, andPredicatePart1), Predicates.eq(secondDescriptor, andPredicatePart2));
        final List<T> foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals(andPredicatePart1, Manipulator.get(foos.get(0), firstDescriptor.getPath()).getObject());
        assertEquals(andPredicatePart2, Manipulator.get(foos.get(0), secondDescriptor.getPath()).getObject());
        assertEquals("(def EQUAL 001_andPredicate) AND (abc EQUAL 002_andPredicate)", predicate.describe());

        assertEquals(2, repository.find(Predicates.eq(firstDescriptor, andPredicatePart1), null, null).size());
        assertEquals(2, repository.find(Predicates.eq(secondDescriptor, andPredicatePart2), null, null).size());
    }

    static <T> void orPredicate(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_orPredicate";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor.getPath(), first);
        repository.save(foo1);

        final var second = "002_orPredicate";
        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor.getPath(), second);
        repository.save(foo2);

        final DescriptivePredicate predicate = Predicates.or(Predicates.eq(descriptor, first), Predicates.eq(descriptor, second));
        final List<T> foos = repository.find(predicate, Comparators.asc(descriptor), null);

        assertEquals(2, foos.size());
        assertEquals(first, Manipulator.get(foos.get(0), descriptor.getPath()).getObject());
        assertEquals(second, Manipulator.get(foos.get(1), descriptor.getPath()).getObject());
        assertEquals("(def EQUAL 001_orPredicate) OR (def EQUAL 002_orPredicate)", predicate.describe());
    }

    static <T> void lowerThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final var lowerThan = "lowerThan";
        final T foo3 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo3, distinctDescriptor.getPath(), lowerThan);
        Manipulator.set(foo3, intDescriptor.getPath(), 7);
        repository.save(foo3);

        final T foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, distinctDescriptor.getPath(), lowerThan);
        Manipulator.set(foo, intDescriptor.getPath(), 10);
        repository.save(foo);

        final DescriptivePredicate predicateLt = Predicates.lt(intDescriptor, 10);
        final var predicate = Predicates.and(Predicates.eq(distinctDescriptor, lowerThan), predicateLt);
        final List<T> foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals(Manipulator.get(foo3, intDescriptor.getPath()).getObject(), Manipulator.get(foos.get(0), intDescriptor.getPath()).getObject());
        assertEquals("value LOWER_THAN 10", predicateLt.describe());
    }

    static <T> void greaterThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final var greaterThan = "greaterThan";
        Manipulator.set(Manipulator.noArgConstructor(clazz), distinctDescriptor.getPath(), greaterThan);
        Manipulator.set(Manipulator.noArgConstructor(clazz), intDescriptor.getPath(), 7);
        final var foo1 = Manipulator.noArgConstructor(clazz);
        repository.save(foo1);

        final T foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, distinctDescriptor.getPath(), greaterThan);
        Manipulator.set(foo, intDescriptor.getPath(), 10);
        repository.save(foo);

        final DescriptivePredicate predicateGt = Predicates.gt(intDescriptor, 7);
        final var predicate = Predicates.and(Predicates.eq(distinctDescriptor, greaterThan), Predicates.gt(intDescriptor, 7));

        final List<T> foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals(Manipulator.get(foo, intDescriptor.getPath()).getObject(), Manipulator.get(foos.get(0), intDescriptor.getPath()).getObject());
        assertEquals("value GREATER_THAN 7", predicateGt.describe());
    }


    static <T> void compoundObject(final Repository<T> repository, final Class<T> clazz, final Class internalClazz, final Descriptor internalDescriptor, final Descriptor compoundDescriptor) {
        final var compoundObject = "compoundObject";
        final T foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, internalDescriptor.getPath(), Manipulator.noArgConstructor(internalClazz));
        Manipulator.set(foo1, compoundDescriptor.getPath(), compoundObject);
        repository.save(foo1);

        final DescriptivePredicate predicate = Predicates.eq(compoundDescriptor, compoundObject);
        final var foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals("bar.str EQUAL compoundObject", predicate.describe());
    }

    static <T> void compoundObjectNullHandling(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor compoundDescriptor) {
        final var compoundObjectNullHandling = "compoundObjectNullHandling";
        final T foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, distinctDescriptor.getPath(), "compoundObjectNullHandling");
        repository.save(foo1);

        final DescriptivePredicate predicate = Predicates.and(Predicates.eq(compoundDescriptor, compoundObjectNullHandling), Predicates.eq(distinctDescriptor, compoundObjectNullHandling));
        final var foos = repository.find(predicate, null, null);

        assertEquals(0, foos.size());
        assertEquals("(bar.str EQUAL compoundObjectNullHandling) AND (def EQUAL compoundObjectNullHandling)", predicate.describe());
    }

    static <T> void nullHandlingLowerThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final T foo3 = Manipulator.noArgConstructor(clazz);
        final var distinct = "nullHandlingLowerThan";
        Manipulator.set(foo3, distinctDescriptor.getPath(), distinct);
        repository.save(foo3);

        final DescriptivePredicate predicate = Predicates.and(Predicates.lt(intDescriptor, 5), Predicates.eq(distinctDescriptor, distinct));
        final var foos = repository.find(predicate, null, null);

        assertEquals(0, foos.size());
        assertEquals("(value LOWER_THAN 5) AND (def EQUAL nullHandlingLowerThan)", predicate.describe());
    }

    static <T> void nullHandlingGreaterThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final T foo3 = Manipulator.noArgConstructor(clazz);
        final var distinct = "nullHandlingGreaterThan";
        Manipulator.set(foo3, distinctDescriptor.getPath(), distinct);
        repository.save(foo3);

        final var predicateGt = Predicates.gt(intDescriptor, 5);
        final var predicate = Predicates.and(Predicates.eq(distinctDescriptor, distinct), predicateGt);
        final var foos = repository.find(predicate, null, null);

        assertEquals(0, foos.size());
        assertEquals("(def EQUAL nullHandlingGreaterThan) AND (value GREATER_THAN 5)", predicate.describe());
    }

    static <T> void sortingAscendingWithNull(final Repository<T> repository, final Class<T> clazz, final Class internalClazz, final Descriptor internalDescriptor, final Descriptor compoundDescriptor,final Descriptor distinctDescriptor) {
        final var sortingAscendingWithNull = "sortingAscendingWithNull";
        final var first = "001_sortingAscendingWithNull";

        final var build1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(build1, distinctDescriptor.getPath(), sortingAscendingWithNull);
        final var internal = Manipulator.noArgConstructor(internalClazz);
        Manipulator.set(build1, internalDescriptor.getPath(), internal);
        Manipulator.set(build1, compoundDescriptor.getPath(), first);
        repository.save(build1);

        final var build = Manipulator.noArgConstructor(clazz);
        Manipulator.set(build, distinctDescriptor.getPath(), sortingAscendingWithNull);
        repository.save(build);

        final var comparator = new DescriptiveComparator(compoundDescriptor, Direction.NATURAL);
        final var predicate = Predicates.eq(distinctDescriptor, sortingAscendingWithNull);
        final var foos = repository.find(predicate, comparator, null);
        assertEquals(2, foos.size());
        assertEquals(null, Manipulator.get(foos.get(0), internalDescriptor.getPath()).getObject());
        assertEquals(first, Manipulator.get(foos.get(1), compoundDescriptor.getPath()).getObject());
        assertEquals("bar.str NATURAL", comparator.describe());
    }

    static <T> void sortingDescendingWithNull(final Repository<T> repository, final Class<T> clazz, final Class internalClazz, final Descriptor internalDescriptor, final Descriptor compoundDescriptor,final Descriptor distinctDescriptor) {
        final var sortingDescendingWithNull = "sortingDescendingWithNull";
        final var build = Manipulator.noArgConstructor(clazz);
        Manipulator.set(build, distinctDescriptor.getPath(), sortingDescendingWithNull);
        repository.save(build);

        final var first = "001_sortingDescendingWithNull";
        final var build1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(build1, distinctDescriptor.getPath(), sortingDescendingWithNull);
        final var internal = Manipulator.noArgConstructor(internalClazz);
        Manipulator.set(build1, internalDescriptor.getPath(), internal);
        Manipulator.set(build1, compoundDescriptor.getPath(), first);
        repository.save(build1);

        final var comparator = new DescriptiveComparator(compoundDescriptor, Direction.REVERSE);
        final var predicate = Predicates.eq(distinctDescriptor, sortingDescendingWithNull);
        final var foos = repository.find(predicate, comparator, null);
        assertEquals(2, foos.size());
        assertEquals(first, Manipulator.get(foos.get(0), compoundDescriptor.getPath()).getObject());
        assertEquals(null, Manipulator.get(foos.get(1), internalDescriptor.getPath()).getObject());
        assertEquals("bar.str REVERSE", comparator.describe());
    }

    static <T> void sequenceContract(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptorSeq) {
        final var saved = repository.save(Manipulator.noArgConstructor(clazz));
        assertEquals(1, Manipulator.get(saved, descriptorSeq.getPath()).getObject());
    }

    static <T> void sequenceContractFollowing(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptorSeq) {
        final var foo1 = Manipulator.noArgConstructor(clazz);
        final var first = repository.save(foo1);

        final var foo2 = Manipulator.noArgConstructor(clazz);
        final var second = repository.save(foo2);
        final var firstSeq = (Comparable)Manipulator.get(first, descriptorSeq.getPath()).getObject();
        final var secondSeq = (Comparable)Manipulator.get(second, descriptorSeq.getPath()).getObject();
        assertTrue(Comparator.<Comparable>naturalOrder().compare(firstSeq, secondSeq) < 0);
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
