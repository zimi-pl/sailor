package pl.zimi.client;

import com.google.gson.Gson;
import pl.zimi.http.Endpoint;
import pl.zimi.http.EndpointsBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class ServiceClientBuilder {

    static <T> T client(Class<T> serviceClass, String baseUrl, HttpClient httpClient) {
        return ProxyProvider.provide(serviceClass, baseUrl, httpClient);
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
