package pl.zimi.http;

public class RequestImpl implements Request {

    private HttpMethod httpMethod;
    private String path;

    public RequestImpl(HttpMethod httpMethod, String path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    @Override
    public String pathParam(String variable) {
        return null;
    }

    @Override
    public String body() {
        return null;
    }
}
