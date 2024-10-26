package pl.sailor.aws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.zimi.client.Request;
import pl.zimi.client.Response;
import pl.zimi.http.Endpoint;
import pl.zimi.http.FakeRequestDecoder;
import pl.zimi.http.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerlessServer implements Server<Object> {

    private final Map<String, EndpointData> endpoints = new HashMap<>();

    @AllArgsConstructor
    @Getter
    static class EndpointData {

        private Endpoint endpoint;
        private List<String> variables;
        private String urlPattern;
    }

    @Override
    public Server setupEndpoint(Endpoint endpoint) {
        EndpointData endpointData = prepareEndpointData(endpoint);
        endpoints.put(endpointData.getUrlPattern(), endpointData);
        return this;
    }

    EndpointData prepareEndpointData(Endpoint endpoint) {
        Pattern pattern = Pattern.compile("\\{([a-zA-Z]+)\\}" );
        Matcher matcher = pattern.matcher(endpoint.getPath());
        List<String> variables = new ArrayList<>();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            variables.add(variableName);
        }

        String urlPattern = pattern.matcher(endpoint.getPath()).replaceAll("([^/]+)");
        return new EndpointData(endpoint, variables, urlPattern);
    }


    @Override
    public Response handleRequest(Request request) {
        EndpointData endpointData = route(request);
        Endpoint endpoint = endpointData.getEndpoint();

        Pattern pattern = Pattern.compile(endpointData.getUrlPattern());
        Matcher matcher = pattern.matcher(request.getUrl());
        List<String> variables = endpointData.getVariables();
        Map<String, String> variableValues = new HashMap<>();
        if (matcher.matches()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String variableValue = matcher.group(i);
                variableValues.put(variables.get(i - 1), variableValue);
            }
        }
        FakeRequestDecoder fakeRequestDecoder = new FakeRequestDecoder(request.getMethod(), request.getUrl(), variableValues, request.getBody());

        String body = endpoint.getScheme().handle(endpoint, fakeRequestDecoder);
        return new Response(body);
    }

    private EndpointData route(Request request) {
        String fullPath = request.getUrl();
        for (String regexUrl : endpoints.keySet()) {
            boolean matches = fullPath.matches(regexUrl);
            if (matches) {
                return endpoints.get(regexUrl);
            }
        }
        return null;
    }

    @Override
    public Object prepare() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public String baseUrl() {
        return null;
    }
}
