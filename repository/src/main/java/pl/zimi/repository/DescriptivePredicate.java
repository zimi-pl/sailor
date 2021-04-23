package pl.zimi.repository;

import java.util.function.Predicate;

public interface DescriptivePredicate extends Predicate {

    String describe();

}
