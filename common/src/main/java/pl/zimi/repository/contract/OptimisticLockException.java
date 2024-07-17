package pl.zimi.repository.contract;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException(final String message) {
        super(message);
    }

}
