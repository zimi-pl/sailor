package pl.zimi.repository;

import ann.Descriptor;

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

    public static DescriptivePredicate lt(Descriptor descriptor, Object value) {
        return new BasicDescriptivePredicate(descriptor, value, Operator.LOWER_THAN);
    }
}
