package pl.zimi.repository.query;

public interface Filter {

    String describe();

    boolean test(Object obj);
}
