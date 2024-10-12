package pl.zimi.client;

import pl.zimi.http.HttpMethod;

public class Request {

    HttpMethod method;
    String url;
    String body;

    public Request(HttpMethod method, String url, String body) {
        this.method = method;
        this.url = url;
        this.body = body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }
}
