package pl.zimi.repository;

import java.util.List;

public class ConjunctionFilter implements Filter {

    static final String AND = "AND";
    static final String OR = "OR";

    private final String operator;
    private final Filter first;
    private final Filter second;

    ConjunctionFilter(final String operator, final Filter first, final Filter second) {
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
