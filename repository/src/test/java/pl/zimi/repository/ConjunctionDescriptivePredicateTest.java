package pl.zimi.repository;

import ann.Descriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConjunctionDescriptivePredicateTest {


    @Test
    void unknownOperator() {
        assertThrows(IllegalArgumentException.class, () -> new ConjunctionDescriptivePredicate("TEST", Predicates.eq(new Descriptor(null, "a"), "bar1"), Predicates.eq(new Descriptor(null, "a"), "bar2")));
    }

}