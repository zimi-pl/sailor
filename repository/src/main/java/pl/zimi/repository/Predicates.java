package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;

public class Predicates {
    public static Filter eq(final Descriptor descriptor, final Object value) {
        return new BasicFilter(descriptor, value, Operator.EQUAL);
    }

    public static Filter and(final Filter first, final Filter second) {
        return new ConjunctionFilter(ConjunctionFilter.AND, first, second);
    }

    public static Filter or(final Filter first, final Filter second) {
        return new ConjunctionFilter(ConjunctionFilter.OR, first, second);
    }

    public static Filter lt(final Descriptor descriptor, final Object value) {
        return new BasicFilter(descriptor, value, Operator.LOWER_THAN);
    }

    public static Filter gt(final Descriptor descriptor, final Object value) {
        return new BasicFilter(descriptor, value, Operator.GREATER_THAN);
    }

    public static Filter regex(final Descriptor descriptor, final String pattern) {
        return new BasicFilter(descriptor, pattern, Operator.REGEX);
    }
}
