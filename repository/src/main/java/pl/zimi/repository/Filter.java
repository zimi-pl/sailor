package pl.zimi.repository;

public interface Filter {

    String describe();

    boolean test(Object obj);
}
