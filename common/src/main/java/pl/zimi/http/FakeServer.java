package pl.zimi.http;

import java.util.ArrayList;
import java.util.List;

public class FakeServer implements Server {

    private final List<Endpoint> endpoints = new ArrayList<>();

    @Override
    public Server setupEndpoint(Endpoint endpoint) {
        endpoints.add(endpoint);
        return this;
    }

    @Override
    public Response handleRequest(Request request) {
        Endpoint endpoint = endpoints.get(0);
        String body = endpoint.getScheme().handle(endpoint, request);
        return new Response(body);
    }
}
