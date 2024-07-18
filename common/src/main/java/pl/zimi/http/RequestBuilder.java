package pl.zimi.http;

public class RequestBuilder {

    private HttpMethod httpMethod;
    private String path;

    private RequestBuilder(HttpMethod httpMethod, String path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public static RequestBuilder get(String path) {
        return new RequestBuilder(HttpMethod.GET, path);
    }

    public Request build() {
        return new RequestImpl(httpMethod, path);
    }
}
