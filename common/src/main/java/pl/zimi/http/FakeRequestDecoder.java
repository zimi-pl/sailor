package pl.zimi.http;

import java.util.Map;

public class FakeRequestDecoder implements RequestDecoder {

    private HttpMethod httpMethod;
    private String path;

    private Map<String, String> pathParams;
    private String body;

    public FakeRequestDecoder(HttpMethod httpMethod, String path, Map<String, String> pathParams, String body) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.pathParams = pathParams;
        this.body = body;
    }

    @Override
    public String fullPath() {
        return path;
    }

    @Override
    public String pathParam(String variable) {
        return pathParams.get(variable);
    }

    @Override
    public String body() {
        return null;
    }
}
