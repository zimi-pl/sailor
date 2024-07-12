package pl.zimi.context;

public class SomeOuterService {

    private final SomeService someService;

    public SomeOuterService(SomeService someService) {
        this.someService = someService;
    }
}
