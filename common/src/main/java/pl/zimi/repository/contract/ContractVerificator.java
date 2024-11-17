package pl.zimi.repository.contract;

import pl.zimi.repository.annotation.Descriptor;
import pl.zimi.repository.annotation.TypedDescriptor;
import pl.zimi.repository.example.Foo;
import pl.zimi.repository.example.SFoo;
import pl.zimi.repository.manipulation.Manipulator;
import pl.zimi.repository.query.*;

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
        final var idDescriptor = SFoo.foo.id;
        final var distinctDescriptor = SFoo.foo.def;
        final var stringDescriptor = SFoo.foo.def;
        final var numberDescriptor = SFoo.foo.value;
        final var compoundDescriptor = SFoo.foo.bar.str;
        final var list = Arrays.asList(
                new Test("saveAndRead", () -> saveAndRead(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor)),
                new Test("independenceAfterSave", () -> independenceAfterSave(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor)),
                new Test("independenceAfterFind", () -> independenceAfterFind(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor)),
                new Test("noFilter", () -> noFilter(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor)),
                new Test("limit", () -> limit(supplier.apply(contract), contract.getEntityClass(), idDescriptor, distinctDescriptor)),
                new Test("andPredicate", () -> andPredicate(supplier.apply(contract), contract.getEntityClass(), numberDescriptor)),
                new Test("orPredicate", () -> orPredicate(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor)),

                new Test("filterStringEqual", () -> filterStringEqual(supplier.apply(contract), contract.getEntityClass(), stringDescriptor, distinctDescriptor)),

                new Test("lowerThan", () -> lowerThan(supplier.apply(contract), contract.getEntityClass(), idDescriptor, distinctDescriptor, numberDescriptor)),
                new Test("greaterThan", () -> greaterThan(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor, numberDescriptor)),
                new Test("isNull", () -> isNull(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor, numberDescriptor)),
                new Test("nullHandlingLowerThan", () -> nullHandlingLowerThan(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor, numberDescriptor)),
                new Test("nullHandlingGreaterThan", () -> nullHandlingGreaterThan(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor, numberDescriptor)),

                new Test("compoundObject", () -> compoundObject(supplier.apply(contract), contract.getEntityClass(), compoundDescriptor)),
                new Test("compoundObjectNullHandling", () -> compoundObjectNullHandling(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor, compoundDescriptor))
        );
        final var tests = new ArrayList<>(list);

        if (contract.getId() == null) {
            tests.add(new Test("missingIdContract", () -> missingIdContract(supplier.apply(contract), contract.getEntityClass())));
            tests.add(new Test("findByIdFailsForMissingIdContract", () -> findByIdFailsForMissingIdContract(supplier.apply(contract))));
        } else {
            tests.add(new Test("existingIdContract", () -> existingIdContract(supplier.apply(contract), contract.getEntityClass())));
            tests.add(new Test("idContract", () -> idContract(supplier.apply(contract), contract.getEntityClass(), contract.getId())));
            tests.add(new Test("idStringContractNextValue", () -> idStringContractNextValue(supplier.apply(contract), contract.getEntityClass(), contract.getId())));
            tests.add(new Test("delete", () -> delete(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor)));
            tests.add(new Test("findByIdWorksForExistingIdContract", () -> findByIdWorksForExistingIdContract(supplier.apply(contract), contract.getEntityClass(), contract.getId())));
            tests.add(new Test("findByIdReturnsEmptyOptionalForExistingIdContract", () -> findByIdReturnsEmptyOptionalForExistingIdContract(supplier.apply(contract))));
        }
        if (contract.isSortingFeature()) {
            tests.add(new Test("sort", () -> sort(supplier.apply(contract), contract.getEntityClass(), stringDescriptor)));
            tests.add(new Test("sortReversed", () -> sortReversed(supplier.apply(contract), contract.getEntityClass(), stringDescriptor)));
            tests.add(new Test("sortingAscendingWithNull", () -> sortingAscendingWithNull(supplier.apply(contract), contract.getEntityClass(), compoundDescriptor, distinctDescriptor)));
            tests.add(new Test("sortingDescendingWithNull", () -> sortingDescendingWithNull(supplier.apply(contract), contract.getEntityClass(), compoundDescriptor, distinctDescriptor)));
        } else {
            tests.add(new Test("sortFails", () -> sortFails(supplier.apply(contract), distinctDescriptor)));
        }
        if (contract.isRegexFeature()) {
            tests.add(new Test("filterStringRegex", () -> filterStringRegex(supplier.apply(contract), contract.getEntityClass(), stringDescriptor)));
        } else {
            tests.add(new Test("regexFails", () -> regexFails(supplier.apply(contract), distinctDescriptor)));
        }
        if (contract.isOffsetFeature()) {
            tests.add(new Test("offset", () -> offset(supplier.apply(contract), contract.getEntityClass(), distinctDescriptor)));
        } else {
            tests.add(new Test("offsetFails", () -> offsetFails(supplier.apply(contract))));
        }

        if (contract.getVersion() != null) {
            tests.add(new Test("versionContract", () -> versionContract(supplier.apply(contract), contract.getEntityClass(), contract.getVersion())));
            tests.add(new Test("versionContractNextValue", () -> versionContractNextValue(supplier.apply(contract), contract.getEntityClass(), contract.getVersion())));
            tests.add(new Test("versionContractOptimisticLock", () -> versionContractOptimisticLock(supplier.apply(contract), contract.getEntityClass(), contract.getId(), contract.getVersion())));
        }
        Collections.shuffle(tests);
        return tests;
    }

    public static void delete(Repository<Foo> repository, Class<Foo> clazz, TypedDescriptor<String> distinctDescriptor) {
        //given
        final var first = "001_delete";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, distinctDescriptor, first);
        final var saved = repository.save(foo);

        //when
        repository.delete(saved);

        //then
        final var all = repository.find(Queries.all());
        final var collect = all.stream().filter(f -> first.equals(Manipulator.get(f, distinctDescriptor).getObject())).collect(Collectors.toList());
        assertEquals(0, collect.size());
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

        final var all = repository.find(Queries.all());
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

        assertEquals(1, repository.find(Queries.filter(Filters.eq(descriptor, first))).size());
        assertEquals(0, repository.find(Queries.filter(Filters.eq(descriptor, second))).size());
    }

    static <T> void independenceAfterFind(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_independenceAfterFind";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final T fooFound = repository.find(Queries.filter(Filters.eq(descriptor, first))).get(0);
        final var second = "002_independenceAfterFind";
        Manipulator.set(fooFound, descriptor, second);

        final var predicate = Filters.or(Filters.eq(descriptor, first), Filters.eq(descriptor, second));
        assertEquals(first, Manipulator.get(repository.find(Queries.filter(predicate)).get(0), descriptor).getObject());
    }

    public static <T> void filterStringEqual(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor, final TypedDescriptor<String> id) {
        final var first = "001_filterStringEqual";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var second = "002_filterStringEqual";
        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor, second);
        repository.save(foo2);

        assertEquals(2L, repository.find(Queries.all()).stream()
                .map(f -> Manipulator.getValue(f, descriptor).getValue())
                .filter(f -> Arrays.asList(first, second).contains(f))
                .count());
        assertEquals(1, repository.find(Queries.filter(Filters.eq(descriptor, first))).size());
    }

    static <T> void filterStringRegex(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_filterStringRegex";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var capitalS = Filters.and(Filters.eq(descriptor, first), Filters.regex(descriptor, "S"));
        assertEquals(1, repository.find(Queries.filter(capitalS)).size());

        final var capitalB = Filters.and(Filters.eq(descriptor, first), Filters.regex(descriptor, "B"));
        assertEquals(0, repository.find(Queries.filter(capitalB)).size());

        final var startsWith = Filters.and(Filters.eq(descriptor, first), Filters.regex(descriptor, "^000"));
        assertEquals(0, repository.find(Queries.filter(startsWith)).size());

        final var startsWithCorrect = Filters.and(Filters.eq(descriptor, first), Filters.regex(descriptor, "^001"));
        assertEquals(1, repository.find(Queries.filter(startsWithCorrect)).size());
    }

    static <T> void noFilter(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor) {
        final var first = "001_noFilter";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var second = "002_noFilter";
        final T foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor, second);
        repository.save(foo2);

        assertEquals(2L, repository.find(Queries.all()).stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.getValue(f, descriptor).getValue()))
                .count());
        assertEquals(2L, repository.find(Queries.filter(null)).stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.getValue(f, descriptor).getValue()))
                .count());

    }

    static <T> void sort(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor) {
        final var first = "002_sort";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var second = "001_sort";
        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor, second);
        repository.save(foo2);

        final List<T> list = repository.find(Queries.query(null, Sorters.asc(descriptor), null));
        final var collected = list.stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.getValue(f, descriptor).getValue()))
                .collect(Collectors.toList());
        assertEquals(2, collected.size());
        assertEquals(second, Manipulator.get(collected.get(0), descriptor).getObject());
        assertEquals(first, Manipulator.get(collected.get(1), descriptor).getObject());

    }

    static <T> void sortReversed(final Repository<T> repository, final Class<T> clazz, final TypedDescriptor<String> descriptor) {
        final var first = "002_sortReversed";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, first);
        repository.save(foo);

        final var second = "001_sortReversed";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor, second);
        repository.save(foo1);

        final List<T> list = repository.find(Queries.query(null, Sorters.desc(descriptor), null));
        final var collected = list.stream()
                .filter(f -> Arrays.asList(first, second).contains(Manipulator.getValue(f, descriptor).getValue()))
                .collect(Collectors.toList());
        assertEquals(2, collected.size());
        assertEquals(first, Manipulator.get(collected.get(0), descriptor).getObject());
        assertEquals(second, Manipulator.get(collected.get(1), descriptor).getObject());

    }

    public static <T> void limit(final Repository<T> repository, final Class<T> clazz, final Descriptor idDescriptor, final Descriptor descriptor) {
        final var second = "002_limit";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, second);
        repository.save(foo);

        final var first = "001_limit";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor, first);
        repository.save(foo1);

        final List<T> list = repository.find(Queries.query(null, null, LimitOffset.limit(1L)));
        assertEquals(1, list.size());
        assertTrue(Arrays.asList(first, second).contains(Manipulator.get(list.get(0), descriptor).getObject()));
    }

    static <T> void offset(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var second = "002_offset";
        final var foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, descriptor, second);
        repository.save(foo);

        final var first = "001_offset";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor, first);
        repository.save(foo1);

        var predicate = Filters.or(Filters.eq(descriptor, first), Filters.eq(descriptor, second));
        final List<T> list = repository.find(Queries.query(predicate, Sorters.asc(descriptor), LimitOffset.limitOffset(null, 1L)));
        assertEquals(1, list.size());
        assertEquals(second, Manipulator.get(list.get(0), descriptor).getObject());
    }

    static <T> void andPredicate(final Repository<T> repository, final Class<T> clazz, final Descriptor intDescriptor) {
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, intDescriptor, 5);
        repository.save(foo1);

        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, intDescriptor, 10);
        repository.save(foo2);

        final var foo3 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo3, intDescriptor, 7);
        repository.save(foo3);

        final var gtPredicate = Filters.gt(intDescriptor, 5);
        final var ltPredicate = Filters.lt(intDescriptor, 10);
        final Filter predicate = Filters.and(gtPredicate, ltPredicate);
        final List<T> foos = repository.find(Queries.filter(predicate));

        assertEquals(1, foos.size());
        assertEquals(7, Manipulator.get(foos.get(0), intDescriptor).getObject());
    }

    static <T> void orPredicate(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptor) {
        final var first = "001_orPredicate";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, descriptor, first);
        repository.save(foo1);

        final var second = "002_orPredicate";
        final var foo2 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo2, descriptor, second);
        repository.save(foo2);

        final Filter predicate = Filters.or(Filters.eq(descriptor, first), Filters.eq(descriptor, second));
        final List<T> foos = repository.find(Queries.filter(predicate));

        assertEquals(2, foos.size());
        Set<String> set = new TreeSet<>();
        set.add((String)Manipulator.get(foos.get(0), descriptor).getObject());
        set.add((String)Manipulator.get(foos.get(1), descriptor).getObject());
        assertEquals(Arrays.asList(first, second), new ArrayList<>(set));
        assertEquals("(def EQUAL 001_orPredicate) OR (def EQUAL 002_orPredicate)", predicate.describe());
    }

    public static <T> void lowerThan(final Repository<T> repository, final Class<T> clazz, final Descriptor idDescriptor, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final var lowerThan = "lowerThan";
        final T foo3 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo3, distinctDescriptor, lowerThan);
        Manipulator.set(foo3, intDescriptor, 7);
        repository.save(foo3);

        final T foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, distinctDescriptor, lowerThan);
        Manipulator.set(foo, intDescriptor, 10);
        repository.save(foo);

        final Filter predicateLt = Filters.lt(intDescriptor, 10);
        final var predicate = Filters.and(Filters.eq(distinctDescriptor, lowerThan), predicateLt);
        final List<T> foos = repository.find(Queries.filter(predicate));

        assertEquals(1, foos.size());
        assertEquals(Manipulator.get(foo3, intDescriptor).getObject(), Manipulator.get(foos.get(0), intDescriptor).getObject());
        assertEquals("value LOWER_THAN 10", predicateLt.describe());
    }

    static <T> void greaterThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final var greaterThan = "greaterThan";
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, distinctDescriptor, greaterThan);
        Manipulator.set(foo1, intDescriptor, 7);
        repository.save(foo1);

        final T foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, distinctDescriptor, greaterThan);
        Manipulator.set(foo, intDescriptor, 10);
        repository.save(foo);

        final Filter predicateGt = Filters.gt(intDescriptor, 7);
        final var predicate = Filters.and(Filters.eq(distinctDescriptor, greaterThan), Filters.gt(intDescriptor, 7));

        final List<T> foos = repository.find(Queries.filter(predicate));

        assertEquals(1, foos.size());
        assertEquals(Manipulator.get(foo, intDescriptor).getObject(), Manipulator.get(foos.get(0), intDescriptor).getObject());
        assertEquals("value GREATER_THAN 7", predicateGt.describe());
    }

    static <T> void isNull(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final var foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, distinctDescriptor, "isNull_withoutNull");
        Manipulator.set(foo1, intDescriptor, 7);
        repository.save(foo1);

        final T foo = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo, distinctDescriptor, "isNull_withNull");
        Manipulator.set(foo, intDescriptor, null);
        repository.save(foo);

        final Filter isNullPredicate = Filters.isNull(intDescriptor);

        final List<T> foos = repository.find(Queries.filter(isNullPredicate));

        assertEquals(1, foos.size());
        assertEquals(Manipulator.get(foo, distinctDescriptor).getObject(), Manipulator.get(foos.get(0), distinctDescriptor).getObject());
        assertEquals("value IS_NULL", isNullPredicate.describe());
    }


    static <T> void compoundObject(final Repository<T> repository, final Class<T> clazz, final Descriptor compoundDescriptor) {
        final Descriptor internalDescriptor = compoundDescriptor.getParent();
        final Class internalClazz = Manipulator.type(clazz, internalDescriptor);
        final var compoundObject = "compoundObject";
        final T foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, internalDescriptor, Manipulator.noArgConstructor(internalClazz));
        Manipulator.set(foo1, compoundDescriptor, compoundObject);
        repository.save(foo1);

        final Filter predicate = Filters.eq(compoundDescriptor, compoundObject);
        final var foos = repository.find(Queries.filter(predicate));

        assertEquals(1, foos.size());
        assertEquals("bar.str EQUAL compoundObject", predicate.describe());
    }

    public static <T> void compoundObjectNullHandling(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor compoundDescriptor) {
        final var compoundObjectNullHandling = "compoundObjectNullHandling";
        final T foo1 = Manipulator.noArgConstructor(clazz);
        Manipulator.set(foo1, distinctDescriptor, "compoundObjectNullHandling");
        repository.save(foo1);

        final Filter predicate = Filters.and(Filters.eq(compoundDescriptor, compoundObjectNullHandling), Filters.eq(distinctDescriptor, compoundObjectNullHandling));
        final var foos = repository.find(Queries.filter(predicate));

        assertEquals(0, foos.size());
        assertEquals("(bar.str EQUAL compoundObjectNullHandling) AND (def EQUAL compoundObjectNullHandling)", predicate.describe());
    }

    static <T> void nullHandlingLowerThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final T foo3 = Manipulator.noArgConstructor(clazz);
        final var distinct = "nullHandlingLowerThan";
        Manipulator.set(foo3, distinctDescriptor, distinct);
        repository.save(foo3);

        final Filter predicate = Filters.and(Filters.lt(intDescriptor, 5), Filters.eq(distinctDescriptor, distinct));
        final var foos = repository.find(Queries.filter(predicate));

        assertEquals(0, foos.size());
        assertEquals("(value LOWER_THAN 5) AND (def EQUAL nullHandlingLowerThan)", predicate.describe());
    }

    static <T> void nullHandlingGreaterThan(final Repository<T> repository, final Class<T> clazz, final Descriptor distinctDescriptor, final Descriptor intDescriptor) {
        final T foo3 = Manipulator.noArgConstructor(clazz);
        final var distinct = "nullHandlingGreaterThan";
        Manipulator.set(foo3, distinctDescriptor, distinct);
        repository.save(foo3);

        final var predicateGt = Filters.gt(intDescriptor, 5);
        final var predicate = Filters.and(Filters.eq(distinctDescriptor, distinct), predicateGt);
        final var foos = repository.find(Queries.filter(predicate));

        assertEquals(0, foos.size());
        assertEquals("(def EQUAL nullHandlingGreaterThan) AND (value GREATER_THAN 5)", predicate.describe());
    }

    static <T> void sortingAscendingWithNull(final Repository<T> repository, final Class<T> clazz, final Descriptor compoundDescriptor,final Descriptor distinctDescriptor) {
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

        final var comparator = new Sorter(compoundDescriptor, Direction.NATURAL);
        final var predicate = Filters.eq(distinctDescriptor, sortingAscendingWithNull);
        final var foos = repository.find(Queries.query(predicate, comparator, null));
        assertEquals(2, foos.size());
        assertEquals(null, Manipulator.get(foos.get(0), internalDescriptor).getObject());
        assertEquals(first, Manipulator.get(foos.get(1), compoundDescriptor).getObject());
        assertEquals("bar.str NATURAL", comparator.describe());
    }

    static <T> void sortingDescendingWithNull(final Repository<T> repository, final Class<T> clazz, final Descriptor compoundDescriptor,final Descriptor distinctDescriptor) {
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

        final var comparator = new Sorter(compoundDescriptor, Direction.REVERSE);
        final var predicate = Filters.eq(distinctDescriptor, sortingDescendingWithNull);
        final var foos = repository.find(Queries.query(predicate, comparator, null));
        assertEquals(2, foos.size());
        assertEquals(first, Manipulator.get(foos.get(0), compoundDescriptor).getObject());
        assertEquals(null, Manipulator.get(foos.get(1), internalDescriptor).getObject());
        assertEquals("bar.str REVERSE", comparator.describe());
    }

    static <T> void sortFails(final Repository<T> repository, final TypedDescriptor<String> descriptor) {
        assertThrows(UnsupportedFeatureException.class, () -> repository.find(Queries.query(null, Sorters.asc(descriptor), null)));
    }

    static <T> void regexFails(final Repository<T> repository, final TypedDescriptor<String> descriptor) {
        assertThrows(UnsupportedFeatureException.class, () -> repository.find(Queries.query(Filters.regex(descriptor, "pattern"), null, null)));
    }

    static <T> void offsetFails(final Repository<T> repository) {
        assertThrows(UnsupportedFeatureException.class, () -> repository.find(Queries.query(null, null, LimitOffset.limitOffset(10L, 10L))));
    }

    static <T> void idContract(final Repository<T> repository, final Class<T> clazz, final Descriptor idDescriptor) {
        final var entity = Manipulator.noArgConstructor(clazz);
        assertEquals(null, Manipulator.get(entity, idDescriptor).getObject());
        final var saved = repository.save(entity);
        assertNotNull(Manipulator.get(saved, idDescriptor).getObject());
    }

    static <T> void idStringContractNextValue(final Repository<T> repository, final Class<T> clazz, final Descriptor idDescriptor) {
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

    static <T> void missingIdContract(final Repository<T> repository, final Class<T> clazz) {
        final var entity = Manipulator.noArgConstructor(clazz);

        T saved = repository.save(entity);
        repository.save(saved);

        final var result = repository.find(Queries.all());

        assertEquals(2, result.size());
    }

    static <T> void findByIdFailsForMissingIdContract(final Repository<T> repository) {
        assertThrows(UnsupportedOperationException.class, () -> repository.findById("test"));
    }

    static <T> void findByIdWorksForExistingIdContract(final Repository<T> repository, final Class<T> clazz, final Descriptor idDescriptor) {
        final var entity = Manipulator.noArgConstructor(clazz);

        final var saved = repository.save(entity);
        final var id = Manipulator.get(saved, idDescriptor).getObject();

        final var retrieved = repository.findById(id).get();
        assertEquals(id, Manipulator.get(retrieved, idDescriptor).getObject());
    }

    static <T> void findByIdReturnsEmptyOptionalForExistingIdContract(final Repository<T> repository) {
        final var retrieved = repository.findById("missing-id");
        assertEquals(Optional.empty(), retrieved);
    }

    static <T> void existingIdContract(final Repository<T> repository, final Class<T> clazz) {
        final var entity = Manipulator.noArgConstructor(clazz);

        T saved = repository.save(entity);
        repository.save(saved);

        final var result = repository.find(Queries.all());

        assertEquals(1, result.size());
    }

    static <T> void versionContract(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptorVersion) {
        final var entity = Manipulator.noArgConstructor(clazz);
        assertEquals(null, Manipulator.get(entity, descriptorVersion).getObject());
        final var saved = repository.save(entity);
        assertEquals(0, Manipulator.get(saved, descriptorVersion).getObject());
    }

    static <T> void versionContractNextValue(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptorVersion) {
        final var entity = Manipulator.noArgConstructor(clazz);
        assertEquals(null, Manipulator.get(entity, descriptorVersion).getObject());
        final var saved = repository.save(entity);
        assertEquals(0, Manipulator.get(saved, descriptorVersion).getObject());

        final var next = repository.save(saved);
        assertEquals(1, Manipulator.get(next, descriptorVersion).getObject());
    }

    static <T> void versionContractOptimisticLock(final Repository<T> repository, final Class<T> clazz, final Descriptor descriptorId, final Descriptor descriptorVersion) {
        final var entity = Manipulator.noArgConstructor(clazz);
        assertEquals(null, Manipulator.get(entity, descriptorVersion).getObject());
        final var saved = repository.save(entity);
        assertEquals(0, Manipulator.get(saved, descriptorVersion).getObject());

        final var next = repository.save(saved);
        assertEquals(1, Manipulator.get(next, descriptorVersion).getObject());

        Manipulator.set(next, descriptorVersion, 0);
        final var lock = assertThrows(OptimisticLockException.class, () -> repository.save(next));
        final var id =  Manipulator.get(next, descriptorId).getObject();
        final var fromDb = repository.find(Queries.filter(Filters.eq(descriptorId, id))).get(0);
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
