package pl.zimi.http;

import pl.zimi.client.Request;
import pl.zimi.client.Response;

import java.util.List;

public interface Server<T> {

    default Server<T> setupService(Object service) {
        List<Endpoint> endpoints = EndpointsBuilder.prepareEndpoints(service);
        for (Endpoint endpoint : endpoints) {
            setupEndpoint(endpoint);
        }
        return this;
    }

    Server<T> setupEndpoint(Endpoint endpoint);

    T prepare();

    void start();

    @Deprecated
    Response handleRequest(Request request);

    String baseUrl();


}
