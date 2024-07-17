package pl.zimi.repository.proxy;

public class ContractException extends RuntimeException {

    public ContractException(Exception ex) {
        super(ex);
    }
}
