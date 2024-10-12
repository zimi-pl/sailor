package pl.zimi.client;

import com.google.gson.Gson;
import pl.zimi.http.Endpoint;
import pl.zimi.http.EndpointsBuilder;
import pl.zimi.http.HttpMethod;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OkhttpClient implements HttpClient {
    @Override
    public Response handleRequest(Request request) {
        try {
            HttpRequest httpRequest = null;
            if (request.getMethod() == HttpMethod.GET) {
                httpRequest = HttpRequest.newBuilder()
                        .uri(new URI(request.getUrl()))
                        .GET()
                        .build();
            } else if (request.getMethod() == HttpMethod.POST) {
                httpRequest = HttpRequest.newBuilder()
                        .uri(new URI(request.getUrl()))
                        .POST(HttpRequest.BodyPublishers.ofString(request.body))
                        .build();
            }

            HttpResponse<String> response = java.net.http.HttpClient.newBuilder()
                    .build()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return new Response(response.body());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    static <T> T client(Class<T> serviceClass, String baseUrl) {
        return ProxyProvider.provide(serviceClass, baseUrl, new OkhttpClient());
    }

    public static class ProxyProvider {

        public static <T> T provide(Class<T> interfaceToProxy, String baseUrl, HttpClient client) {
            List<Endpoint> endpoints = EndpointsBuilder.prepareEndpoints(interfaceToProxy);

            InvocationHandler handler = new EndpointInvocationHandler(endpoints, baseUrl, client, interfaceToProxy);
            return (T) Proxy.newProxyInstance(interfaceToProxy.getClassLoader(),
                    new Class[] { interfaceToProxy },
                    handler);
        }
    }

    public static class EndpointInvocationHandler implements InvocationHandler {

        List<Endpoint> endpointList;
        String baseUrl;
        HttpClient client;
        Class interfaceClass;

        public EndpointInvocationHandler(List<Endpoint> endpointList, String baseUrl, HttpClient client, Class interfaceClass) {
            this.endpointList = endpointList;
            this.baseUrl = baseUrl;
            this.client = client;
            this.interfaceClass = interfaceClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Class returnType = method.getReturnType();
            Endpoint endpoint = chooseEndpoint(proxy, method);
            String path = endpoint.getPath();
            String body;
            if (method.getName().equals("find") || method.getName().equals("get")) {
                path = path.replace("{id}", (String)args[0]);
                body = null;
            } else {
                Gson gson = new Gson();
                body = gson.toJson(args[0]);
            }
            String url = baseUrl + path;
            Request request = new Request(endpoint.getMethod(), url, body);
            Response response = client.handleRequest(request);
            Gson gson = new Gson();
            return gson.fromJson(response.body(), returnType);
        }

        private Endpoint chooseEndpoint(Object proxy, Method method) {
            Endpoint endpoint = EndpointsBuilder.prepareEndpoint(proxy, method, EndpointsBuilder.preparePath(interfaceClass));
            Endpoint found = endpointList.stream().filter(it -> isSameEndpoint(it, endpoint)).findFirst().get();
            return found;
        }

        private static boolean isSameEndpoint(Endpoint it, Endpoint endpoint) {
            return it.getPath().equals(endpoint.getPath()) && it.getMethod().equals(endpoint.getMethod());
        }
    }
}
