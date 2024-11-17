package pl.zimi.repository.contract;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException(final String message) {
        super(message);
    }

    public OptimisticLockException(final String message, final Exception exception) {
        super(message, exception);
    }

}
