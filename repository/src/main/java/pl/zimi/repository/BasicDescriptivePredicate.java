package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;

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
        final Value computedValue = Manipulator.get(o, path.getPath());
        if (computedValue.getFailureReason() == null) {
            switch (operator) {
                case EQUAL:
                    return Objects.equals(computedValue.getObject(), expectedValue);
                case LOWER_THAN:
                    return compare(computedValue.getObject(), expectedValue) < 0;
                case GREATER_THAN:
                    return compare(computedValue.getObject(), expectedValue) > 0;
                default:
                    throw new IllegalArgumentException("Unknown operator " + operator);
            }
        } else {
            return false;
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
