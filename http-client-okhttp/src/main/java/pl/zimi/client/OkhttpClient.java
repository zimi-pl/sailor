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

}
