package pl.zimi.client;

import pl.zimi.http.Endpoint;
import pl.zimi.http.EndpointsBuilder;
import pl.zimi.repository.proxy.SimpleInvocationHandler;
import pl.zimi.repository.query.Repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

public interface HttpClient {

    public Response handleRequest(Request request);

    default <T> T createClient(Class<T> service, String baseUrl) {
        List<Endpoint> endpoints = EndpointsBuilder.prepareEndpoints(service);
        Endpoint endpoint = endpoints.get(0);
        String url = baseUrl + endpoint.getPath();
//
//        new Request(endpoint.getMethod(), url, )
//        return
        return null;
    }



}
