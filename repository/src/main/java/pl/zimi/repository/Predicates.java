package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;

public class Predicates {
    public static DescriptivePredicate eq(final Descriptor descriptor, final Object value) {
        return new BasicDescriptivePredicate(descriptor, value, Operator.EQUAL);
    }

    public static DescriptivePredicate and(final DescriptivePredicate first, final DescriptivePredicate second) {
        return new ConjunctionDescriptivePredicate(ConjunctionDescriptivePredicate.AND, first, second);
    }

    public static DescriptivePredicate or(final DescriptivePredicate first, final DescriptivePredicate second) {
        return new ConjunctionDescriptivePredicate(ConjunctionDescriptivePredicate.OR, first, second);
    }

    public static DescriptivePredicate lt(final Descriptor descriptor, final Object value) {
        return new BasicDescriptivePredicate(descriptor, value, Operator.LOWER_THAN);
    }

    public static DescriptivePredicate gt(final Descriptor descriptor, final Object value) {
        return new BasicDescriptivePredicate(descriptor, value, Operator.GREATER_THAN);
    }

    public static DescriptivePredicate regex(final Descriptor descriptor, final String pattern) {
        return new BasicDescriptivePredicate(descriptor, pattern, Operator.REGEX);
    }
}
