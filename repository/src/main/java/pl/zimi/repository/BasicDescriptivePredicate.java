package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;

import java.util.Objects;
import java.util.regex.Pattern;

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
        final Value computedValue = Manipulator.get(o, path.getPath());
        if (computedValue.getFailureReason() == null) {
            switch (operator) {
                case EQUAL:
                    return Objects.equals(computedValue.getObject(), expectedValue);
                case LOWER_THAN:
                    return isLowerThan(computedValue.getObject());
                case GREATER_THAN:
                    return isGreaterThan(computedValue.getObject());
                case REGEX:
                    return computedValue.getObject() != null && Pattern.compile((String)expectedValue).matcher((String)computedValue.getObject()).find();
                default:
                    throw new IllegalArgumentException("Unknown operator " + operator);
            }
        } else {
            return false;
        }
    }

    private boolean isGreaterThan(final Object computedValue) {
        return computedValue != null && compare(computedValue, expectedValue) > 0;
    }

    private boolean isLowerThan(final Object computedValue) {
        return computedValue != null && compare(computedValue, expectedValue) < 0;
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
