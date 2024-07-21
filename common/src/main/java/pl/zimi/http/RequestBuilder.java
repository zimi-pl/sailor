package pl.zimi.http;

public class RequestBuilder {

    private HttpMethod httpMethod;
    private String path;
    private String body;

    private RequestBuilder(HttpMethod httpMethod, String path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public static RequestBuilder get(String path) {
        return new RequestBuilder(HttpMethod.GET, path);
    }

    public RequestBuilder body(String body) {
        this.body = body;
        return this;
    }

    public Request build() {
        return new Request(httpMethod, path, body);
    }
}
