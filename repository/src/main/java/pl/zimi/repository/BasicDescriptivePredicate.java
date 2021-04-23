package pl.zimi.repository;

import ann.Descriptor;

import java.util.Objects;

class BasicDescriptivePredicate implements DescriptivePredicate {

    private final Descriptor path;
    private final Object expectedValue;
    private final Operator operator;

    BasicDescriptivePredicate(Descriptor path, Object expectedValue, Operator operator) {
        this.path = path;
        this.expectedValue = expectedValue;
        this.operator = operator;
    }

    public String getPath() {
        return path.getPath();
    }

    public Object getExpectedValue() {
        return expectedValue;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String describe() {
        return getPath() + " " + getOperator() + " " + getExpectedValue();
    }

    @Override
    public boolean test(Object o) {
        final Object computedValue = Manipulator.get(o, path.getPath());
        switch (operator) {
            case EQUAL:
                return Objects.equals(computedValue, expectedValue);
            case LOWER_THAN:
                return compare(computedValue, expectedValue) < 0;
            default:
                throw new IllegalArgumentException("Unknown operator " + operator);

        }
    }

    private int compare(Object computedValue, Object expectedValue) {
        if (!(computedValue instanceof Comparable)) {
            throw new IllegalArgumentException("Wrong uncomparable value " + computedValue);
        }
        if (!(expectedValue instanceof Comparable)) {
            throw new IllegalArgumentException("Wrong uncomparable value " + expectedValue);
        }

        return ((Comparable)computedValue).compareTo(expectedValue);
    }
}
