package pl.zimi.repository;

import java.util.List;

public class ConjunctionDescriptivePredicate implements DescriptivePredicate {

    static final String AND = "AND";
    static final String OR = "OR";

    private final String operator;
    private final DescriptivePredicate first;
    private final DescriptivePredicate second;

    ConjunctionDescriptivePredicate(final String operator, final DescriptivePredicate first, final DescriptivePredicate second) {
        this.operator = operator;
        assertOperator();
        this.first = first;
        this.second = second;
    }

    @Override
    public String describe() {
        return "(" + first.describe() + ") " + operator + " (" + second.describe() + ")";
    }

    @Override
    public boolean test(Object o) {
        return combine(first.test(o), second.test(o));
    }

    private boolean combine(final boolean first, final boolean second) {
        if (AND.equals(operator)) {
            return first && second;
        } else if (OR.equals(operator)) {
            return first || second;
        }
        throw new RuntimeException("unreachable part");
    }

    final void assertOperator() {
        if (!List.of(AND, OR).contains(operator)) {
            throw new IllegalArgumentException("Unknown operator");
        }
    }
}
