package pl.zimi.http;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pl.zimi.repository.annotation.Descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Builder
@Getter
@ToString
public class Endpoint {
    private HttpMethod method;
    private String path;
    private Function handler;
    private Scheme scheme;
    private List<Mapping> mappings;
    private Class requestClass;

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

        public EndpointBuilder mapping(String variableName, Descriptor descriptor) {
            if (mappings == null) {
                mappings = new ArrayList<>();
            }
            mappings.add(new Mapping(variableName, descriptor));
            return this;
        }
    }

}
