package pl.zimi.repository.contract;

public class UnsupportedFeatureException extends RuntimeException {
    public UnsupportedFeatureException(String message) {
        super(message);
    }
}
