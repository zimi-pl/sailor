package pl.zimi.http;

import pl.zimi.repository.annotation.Descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Endpoint {
    private HttpMethod method;
    private String path;
    private Function handler;
    private Scheme scheme;
    private List<Mapping> mappings;
    private Class requestClass;

    public Endpoint(HttpMethod method, String path, Function handler, Scheme scheme, List<Mapping> mappings, Class requestClass) {
        this.method = method;
        this.path = path;
        this.handler = handler;
        this.scheme = scheme;
        this.mappings = mappings;
        this.requestClass = requestClass;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Function getHandler() {
        return handler;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public List<Mapping> getMappings() {
        return mappings;
    }

    public Class getRequestClass() {
        return requestClass;
    }

    public static EndpointBuilder get() {
        return builder().method(HttpMethod.GET);
    }

    public static EndpointBuilder post() {
        return builder().method(HttpMethod.POST);
    }

    public static EndpointBuilder put() {
        return builder().method(HttpMethod.PUT);
    }

    public static EndpointBuilder delete() {
        return builder().method(HttpMethod.DELETE);
    }

    public static class EndpointBuilder {

        private List<Mapping> mappings;

        private HttpMethod method;

        private String path;

        Class requestClass;

        Function handler;

        Scheme scheme;

        public EndpointBuilder mapping(String variableName, Descriptor descriptor) {
            if (mappings == null) {
                mappings = new ArrayList<>();
            }
            mappings.add(new Mapping(variableName, descriptor));
            return this;
        }

        public EndpointBuilder path(String path) {
            this.path = path;
            return this;
        }
        public EndpointBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }
        public EndpointBuilder scheme(Scheme scheme) {
            this.scheme = scheme;
            return this;
        }
        public EndpointBuilder handler(Function handler) {
            this.handler = handler;
            return this;
        }
        public EndpointBuilder requestClass(Class requestClass) {
            this.requestClass = requestClass;
            return this;
        }

        public Endpoint build() {
            return new Endpoint(method, path, handler, scheme, mappings, requestClass);
        }
    }



    public static EndpointBuilder builder() {
        return new EndpointBuilder();
    }

}
