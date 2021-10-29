package pl.zimi.repository.contract;

import pl.zimi.repository.Comparators;
import pl.zimi.repository.*;
import pl.zimi.repository.annotation.Descriptor;
import pl.zimi.repository.annotation.TypedDescriptor;

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
        final var distinctDescriptor = SFoo.foo.def;
        final var stringDescriptor = SFoo.foo.def;
        final var numberDescriptor = SFoo.foo.value;
        final var compoundDescriptor = SFoo.foo.bar.str;
        final var list = Arrays.asList(
                new Test("saveAndRead", () -> saveAndRead(repository, contract.getEntityClass(), distinctDescriptor)),
                new Test("independenceAfterSave", () -> independenceAfterSave(repository, contract.getEntityClass(), distinctDescriptor)),
                new Test("independenceAfterFind", () -> independenceAfterFind(repository, contract.getEntityClass(), distinctDescriptor)),
                new Test("noFilter", () -> noFilter(repository, contract.getEntityClass(), distinctDescriptor)),
                new Test("limit", () -> limit(repository, contract.getEntityClass(), distinctDescriptor)),
                new Test("offset", () -> offset(repository, contract.getEntityClass(), distinctDescriptor)),
                new Test("andPredicate", () -> andPredicate(repository, contract.getEntityClass(), distinctDescriptor)),
                new Test("orPredicate", () -> orPredicate(repository, contract.getEntityClass(), distinctDescriptor)),

                new Test("filterStringEqual", () -> filterStringEqual(repository, contract.getEntityClass(), stringDescriptor)),
                new Test("filterStringRegex", () -> filterStringRegex(repository, contract.getEntityClass(), stringDescriptor)),
                new Test("sort", () -> sort(repository, contract.getEntityClass(), stringDescriptor)),
                new Test("sortReversed", () -> sortReversed(repository, contract.getEntityClass(), stringDescriptor)),

                new Test("lowerThan", () -> lowerThan(repository, contract.getEntityClass(), distinctDescriptor, numberDescriptor)),
                new Test("greaterThan", () -> greaterThan(repository, contract.getEntityClass(), distinctDescriptor, numberDescriptor)),
                new Test("nullHandlingLowerThan", () -> nullHandlingLowerThan(repository, contract.getEntityClass(), distinctDescriptor, numberDescriptor)),
                new Test("nullHandlingGreaterThan", () -> nullHandlingGreaterThan(repository, contract.getEntityClass(), distinctDescriptor, numberDescriptor)),

                new Test("compoundObject", () -> compoundObject(repository, contract.getEntityClass(), compoundDescriptor)),
                new Test("compoundObjectNullHandling", () -> compoundObjectNullHandling(repository, contract.getEntityClass(), distinctDescriptor, compoundDescriptor)),
                new Test("sortingAscendingWithNull", () -> sortingAscendingWithNull(repository, contract.getEntityClass(), compoundDescriptor, distinctDescriptor)),
                new Test("sortingDescendingWithNull", () -> sortingDescendingWithNull(repository, contract.getEntityClass(), compoundDescriptor, distinctDescriptor))
        );
        final var tests = new ArrayList<>(list);

        if (contract.getId() != null) {
            tests.add(new Test("idStringContract", () -> idStringContract(supplier.apply(contract), contract.getEntityClass(), contract.getId())));
            tests.add(new Test("idStringContractNextValue", () -> idStringContractNextValue(supplier.apply(contract), contract.getEntityClass(), contract.getId())));
        }

        if (contract.getVersion() != null) {
            tests.add(new Test("versionContract", () -> versionContract(supplier.apply(contract), contract.getEntityClass(), contract.getVersion())));
            tests.add(new Test("versionContractNextValue", () -> versionContractNextValue(supplier.apply(contract), contract.getEntityClass(), contract.getVersion())));
            tests.add(new Test("versionContractOptimisticLock", () -> versionContractOptimisticLock(supplier.apply(contract), contract.getEntityClass(), contract.getId(), contract.getVersion())));
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

    public static <T> void saveAndRead(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor) {
        final var first = "001_saveAndRead";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);

        //change type to other
        repository.save(foo);

        final var all = repository.findAll();
        final var collect = all.stream().filter(f -> first.equals(Manipulator.get(f, descriptor).getObject())).collect(Collectors.toList());
        assertEquals(1, collect.size());
        assertEquals(first, Manipulator.get(collect.get(0), descriptor).getObject());
    }

    public static <T> void independenceAfterSave(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor) {
        final var first = "001_independenceAfterSave";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var second = "002_independenceAfterSave";
        Manipulator.set(foo, descriptor, second);

        assertEquals(1, repository.find(Predicates.eq(descriptor, first), null, null).size());
        assertEquals(0, repository.find(Predicates.eq(descriptor, second), null, null).size());
    }

    public static <T> void independenceAfterFind(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_independenceAfterFind";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final T fooFound = repository.find(Predicates.eq(descriptor, first), null, null).get(0);
        final var second = "002_independenceAfterFind";
        Manipulator.set(fooFound, descriptor, second);

        final var predicate = Predicates.or(Predicates.eq(descriptor, first), Predicates.eq(descriptor, second));
        assertEquals(first, Manipulator.get(repository.find(predicate, null, null).get(0), descriptor).getObject());
    }

    public static <T> void filterStringEqual(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor) {
        final var first = "001_filterStringEqual";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var second = "002_filterStringEqual";
        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor, second);
        repository.save(foo2);

        assertEquals(2L, repository.findAll().stream()
                .map(f -> Manipulator.getValue(f, descriptor).getValue())
                .filter(f -> Arrays.asList(first, second).contains(f))
                .count());
        assertEquals(1, repository.find(Predicates.eq(descriptor, first), null, null).size());
    }

    public static <T> void filterStringRegex(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_filterStringRegex";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
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

    public static <T> void noFilter(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor) {
        final var first = "001_noFilter";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var second = "002_noFilter";
        final T foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor, second);
        repository.save(foo2);

        assertEquals(2L, repository.findAll().stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.getValue(f, descriptor).getValue()))
                .count());
        assertEquals(2L, repository.find(null, null, null).stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.getValue(f, descriptor).getValue()))
                .count());

    }

    public static <T> void sort(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor) {
        final var first = "002_sort";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var second = "001_sort";
        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor, second);
        repository.save(foo2);

        final List<T> list = repository.find(null, Comparators.asc(descriptor), null);
        final var collected = list.stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.getValue(f, descriptor).getValue()))
                .collect(Collectors.toList());
        assertEquals(2, collected.size());
        assertEquals(second, Manipulator.get(collected.get(0), descriptor).getObject());
        assertEquals(first, Manipulator.get(collected.get(1), descriptor).getObject());

    }

    public static <T> void sortReversed(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor) {
        final var first = "002_sortReversed";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var second = "001_sortReversed";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor, second);
        repository.save(foo1);

        final List<T> list = repository.find(null, Comparators.desc(descriptor), null);
        final var collected = list.stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.getValue(f, descriptor).getValue()))
                .collect(Collectors.toList());
        assertEquals(2, collected.size());
        assertEquals(first, Manipulator.get(collected.get(0), descriptor).getObject());
        assertEquals(second, Manipulator.get(collected.get(1), descriptor).getObject());

    }

    public static <T> void limit(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var second = "002_limit";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, second);
        repository.save(foo);

        final var first = "001_limit";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor, first);
        repository.save(foo1);

        final var a = Predicates.eq(descriptor, second);
        final var b = Predicates.eq(descriptor, first);
        final List<T> list = repository.find(Predicates.or(a, b), Comparators.asc(descriptor), new LimitOffset(1L, null));
        assertEquals(1, list.size());
        assertEquals(first, Manipulator.get(list.get(0), descriptor).getObject());
    }

    public static <T> void offset(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var second = "002_offset";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, second);
        repository.save(foo);

        final var first = "001_offset";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor, first);
        repository.save(foo1);

        var predicate = Predicates.or(Predicates.eq(descriptor, first), Predicates.eq(descriptor, second));
        final List<T> list = repository.find(predicate, Comparators.asc(descriptor), new LimitOffset(null, 1L));
        assertEquals(1, list.size());
        assertEquals(second, Manipulator.get(list.get(0), descriptor).getObject());
    }

    public static <T> void andPredicate(final Repository<T> repository, final Class<T> clazz, final Descriptor firstDescriptor) {
        final var andPredicatePart1 = "001_andPredicate";

        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, firstDescriptor, andPredicatePart1);
        repository.save(foo1);

        final var andPredicatePart2 = "002_andPredicate";

        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, firstDescriptor, andPredicatePart2);
        repository.save(foo2);

        final var andPredicate = Predicates.regex(firstDescriptor, "_andPredicate$");
        final Filter predicate = Predicates.and(Predicates.regex(firstDescriptor, "^001_"), andPredicate);
        final List<T> foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals(andPredicatePart1, Manipulator.get(foos.get(0), firstDescriptor).getObject());

        assertEquals(2, repository.find(andPredicate, null, null).size());
    }

    public static <T> void orPredicate(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_orPredicate";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor, first);
        repository.save(foo1);

        final var second = "002_orPredicate";
        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor, second);
        repository.save(foo2);

        final Filter predicate = Predicates.or(Predicates.eq(descriptor, first), Predicates.eq(descriptor, second));
        final List<T> foos = repository.find(predicate, Comparators.asc(descriptor), null);

        assertEquals(2, foos.size());
        assertEquals(first, Manipulator.get(foos.get(0), descriptor).getObject());
        assertEquals(second, Manipulator.get(foos.get(1), descriptor).getObject());
        assertEquals("(def EQUAL 001_orPredicate) OR (def EQUAL 002_orPredicate)", predicate.describe());
    }

    public static <T> void lowerThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final var lowerThan = "lowerThan";
        final T foo3 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo3, distinctDescriptor, lowerThan);
        Manipulator.set(foo3, intDescriptor, 7);
        repository.save(foo3);

        final T foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, distinctDescriptor, lowerThan);
        Manipulator.set(foo, intDescriptor, 10);
        repository.save(foo);

        final Filter predicateLt = Predicates.lt(intDescriptor, 10);
        final var predicate = Predicates.and(Predicates.eq(distinctDescriptor, lowerThan), predicateLt);
        final List<T> foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals(Manipulator.get(foo3, intDescriptor).getObject(), Manipulator.get(foos.get(0), intDescriptor).getObject());
        assertEquals("value LOWER_THAN 10", predicateLt.describe());
    }

    public static <T> void greaterThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final var greaterThan = "greaterThan";
        Manipulator.set(Manipulator.noArgConstructor(clazz), distinctDescriptor, greaterThan);
        Manipulator.set(Manipulator.noArgConstructor(clazz), intDescriptor, 7);
        final var foo1 = Manipulator.noArgConstructor(clazz);
        repository.save(foo1);

        final T foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, distinctDescriptor, greaterThan);
        Manipulator.set(foo, intDescriptor, 10);
        repository.save(foo);

        final Filter predicateGt = Predicates.gt(intDescriptor, 7);
        final var predicate = Predicates.and(Predicates.eq(distinctDescriptor, greaterThan), Predicates.gt(intDescriptor, 7));

        final List<T> foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals(Manipulator.get(foo, intDescriptor).getObject(), Manipulator.get(foos.get(0), intDescriptor).getObject());
        assertEquals("value GREATER_THAN 7", predicateGt.describe());
    }


    public static <T> void compoundObject(final Repository<T> repository, final Class<T> clazz, final Descriptor compoundDescriptor) {
        final Descriptor internalDescriptor = compoundDescriptor.getParent();
        final Class internalClazz = Manipulator.type(clazz, internalDescriptor);
        final var compoundObject = "compoundObject";
        final T foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, internalDescriptor, Manipulator.noArgConstructor(internalClazz));
        Manipulator.set(foo1, compoundDescriptor, compoundObject);
        repository.save(foo1);

        final Filter predicate = Predicates.eq(compoundDescriptor, compoundObject);
        final var foos = repository.find(predicate, null, null);

        assertEquals(1, foos.size());
        assertEquals("bar.str EQUAL compoundObject", predicate.describe());
    }

    public static <T> void compoundObjectNullHandling(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor compoundDescriptor) {
        final var compoundObjectNullHandling = "compoundObjectNullHandling";
        final T foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, distinctDescriptor, "compoundObjectNullHandling");
        repository.save(foo1);

        final Filter predicate = Predicates.and(Predicates.eq(compoundDescriptor, compoundObjectNullHandling), Predicates.eq(distinctDescriptor, compoundObjectNullHandling));
        final var foos = repository.find(predicate, null, null);

        assertEquals(0, foos.size());
        assertEquals("(bar.str EQUAL compoundObjectNullHandling) AND (def EQUAL compoundObjectNullHandling)", predicate.describe());
    }

    public static <T> void nullHandlingLowerThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final T foo3 = Manipulator.noArgConstructor(clazz);
        final var distinct = "nullHandlingLowerThan";
        Manipulator.set(foo3, distinctDescriptor, distinct);
        repository.save(foo3);

        final Filter predicate = Predicates.and(Predicates.lt(intDescriptor, 5), Predicates.eq(distinctDescriptor, distinct));
        final var foos = repository.find(predicate, null, null);

        assertEquals(0, foos.size());
        assertEquals("(value LOWER_THAN 5) AND (def EQUAL nullHandlingLowerThan)", predicate.describe());
    }

    public static <T> void nullHandlingGreaterThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final T foo3 = Manipulator.noArgConstructor(clazz);
        final var distinct = "nullHandlingGreaterThan";
        Manipulator.set(foo3, distinctDescriptor, distinct);
        repository.save(foo3);

        final var predicateGt = Predicates.gt(intDescriptor, 5);
        final var predicate = Predicates.and(Predicates.eq(distinctDescriptor, distinct), predicateGt);
        final var foos = repository.find(predicate, null, null);

        assertEquals(0, foos.size());
        assertEquals("(def EQUAL nullHandlingGreaterThan) AND (value GREATER_THAN 5)", predicate.describe());
    }

    public static <T> void sortingAscendingWithNull(final Repository<T> repository, final Class<T> clazz, final Descriptor compoundDescriptor,final Descriptor distinctDescriptor) {
        final Descriptor internalDescriptor = compoundDescriptor.getParent();
        final Class internalClazz = Manipulator.type(clazz, internalDescriptor);
        final var sortingAscendingWithNull = "sortingAscendingWithNull";
        final var first = "001_sortingAscendingWithNull";

        final var build1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(build1, distinctDescriptor, sortingAscendingWithNull);
        final var internal = Manipulator.noArgConstructor(internalClazz);
        Manipulator.set(build1, internalDescriptor, internal);
        Manipulator.set(build1, compoundDescriptor, first);
        repository.save(build1);

        final var build = Manipulator.noArgConstructor(clazz);
        Manipulator.set(build, distinctDescriptor, sortingAscendingWithNull);
        repository.save(build);

        final var comparator = new Sort(compoundDescriptor, Direction.NATURAL);
        final var predicate = Predicates.eq(distinctDescriptor, sortingAscendingWithNull);
        final var foos = repository.find(predicate, comparator, null);
        assertEquals(2, foos.size());
        assertEquals(null, Manipulator.get(foos.get(0), internalDescriptor).getObject());
        assertEquals(first, Manipulator.get(foos.get(1), compoundDescriptor).getObject());
        assertEquals("bar.str NATURAL", comparator.describe());
    }

    public static <T> void sortingDescendingWithNull(final Repository<T> repository, final Class<T> clazz, final Descriptor compoundDescriptor,final Descriptor distinctDescriptor) {
        final Descriptor internalDescriptor = compoundDescriptor.getParent();
        final Class internalClazz = Manipulator.type(clazz, internalDescriptor);
        final var sortingDescendingWithNull = "sortingDescendingWithNull";
        final var build = Manipulator.noArgConstructor(clazz);
        Manipulator.set(build, distinctDescriptor, sortingDescendingWithNull);
        repository.save(build);

        final var first = "001_sortingDescendingWithNull";
        final var build1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(build1, distinctDescriptor, sortingDescendingWithNull);
        final var internal = Manipulator.noArgConstructor(internalClazz);
        Manipulator.set(build1, internalDescriptor, internal);
        Manipulator.set(build1, compoundDescriptor, first);
        repository.save(build1);

        final var comparator = new Sort(compoundDescriptor, Direction.REVERSE);
        final var predicate = Predicates.eq(distinctDescriptor, sortingDescendingWithNull);
        final var foos = repository.find(predicate, comparator, null);
        assertEquals(2, foos.size());
        assertEquals(first, Manipulator.get(foos.get(0), compoundDescriptor).getObject());
        assertEquals(null, Manipulator.get(foos.get(1), internalDescriptor).getObject());
        assertEquals("bar.str REVERSE", comparator.describe());
    }

    public static <T> void idStringContract(final Repository<T> repository, final Class<T> clazz, final Descriptor idDescriptor) {
        final var entity = Manipulator.noArgConstructor(clazz);
        assertEquals(null, Manipulator.get(entity, idDescriptor).getObject());
        final var saved = repository.save(entity);
        assertNotNull(Manipulator.get(saved, idDescriptor).getObject());
    }

    public static <T> void idStringContractNextValue(final Repository<T> repository, final Class<T> clazz, final Descriptor idDescriptor) {
        final var entity = Manipulator.noArgConstructor(clazz);
        assertEquals(null, Manipulator.get(entity, idDescriptor).getObject());
        final var first = repository.save(entity);
        final var firstId = Manipulator.get(first, idDescriptor).getObject();
        assertNotNull(firstId);

        final var second = repository.save(entity);
        final var secondId = Manipulator.get(second, idDescriptor).getObject();
        assertNotNull(secondId);
        assertNotEquals(firstId, secondId);

    }

    public static <T> void versionContract(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptorVersion) {
        final var entity = Manipulator.noArgConstructor(clazz);
        assertEquals(null, Manipulator.get(entity, descriptorVersion).getObject());
        final var saved = repository.save(entity);
        assertEquals(0, Manipulator.get(saved, descriptorVersion).getObject());
    }

    public static <T> void versionContractNextValue(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptorVersion) {
        final var entity = Manipulator.noArgConstructor(clazz);
        assertEquals(null, Manipulator.get(entity, descriptorVersion).getObject());
        final var saved = repository.save(entity);
        assertEquals(0, Manipulator.get(saved, descriptorVersion).getObject());

        final var next = repository.save(saved);
        assertEquals(1, Manipulator.get(next, descriptorVersion).getObject());
    }

    public static <T> void versionContractOptimisticLock(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptorId, final Descriptor descriptorVersion) {
        final var entity = Manipulator.noArgConstructor(clazz);
        assertEquals(null, Manipulator.get(entity, descriptorVersion).getObject());
        final var saved = repository.save(entity);
        assertEquals(0, Manipulator.get(saved, descriptorVersion).getObject());

        final var next = repository.save(saved);
        assertEquals(1, Manipulator.get(next, descriptorVersion).getObject());

        Manipulator.set(next, descriptorVersion, 0);
        final var lock = assertThrows(OptimisticLockException.class, () -> repository.save(next));
        final var id =  Manipulator.get(next, descriptorId).getObject();
        final var fromDb = repository.find(Predicates.eq(descriptorId, id), null, null).get(0);
        assertEquals(1, Manipulator.get(fromDb, descriptorVersion).getObject());

    }

    private static <T> T assertThrows(final Class<T> clazz, final Executable executable) {
        try {
            executable.execute();
            throw new RuntimeException("Exception was not thrown.");
        } catch (final Exception ex) {
            if (clazz.isInstance(ex)) {
                return (T) ex;
            } else {
                throw new RuntimeException("Unexpected type of exception", ex);
            }
        }
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

    private static void assertNotEquals(final Object expected, final Object actual) {
        if (Objects.equals(expected, actual)) {
            throw new RuntimeException("Values are equals but they should not");
        }
    }

    private static void assertNotNull(final Object value) {
        if (value == null) {
            throw new RuntimeException("value is null");
        }
    }

}
