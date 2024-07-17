package pl.zimi.http;

import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;

import java.util.List;

public class JavalinPort {

    private Javalin javalin;

    public JavalinPort(Javalin javalin) {
        this.javalin = javalin;
    }

    public JavalinPort setupService(Object service) {
        List<Endpoint> endpoints = Server.prepareEndpoints(service);
        for (Endpoint endpoint : endpoints) {
            setupEndpoint(endpoint);
        }
        return this;
    }

    public JavalinPort setupEndpoint(Endpoint endpoint) {
        System.out.println(endpoint);
        Handler handler = ctx -> {
            Request request = new JavalinRequest(ctx);
            String json = endpoint.scheme.handle(endpoint, request);
            ctx.contentType(ContentType.APPLICATION_JSON).result(json);
        };
        javalin.addHttpHandler(prepareHandlerType(endpoint), endpoint.getPath(), handler);
        return this;
    }

    private static HandlerType prepareHandlerType(Endpoint endpoint) {
        switch (endpoint.getMethod()) {
            case GET: return HandlerType.GET;
            case POST: return HandlerType.POST;
            case PUT: return HandlerType.PUT;
            case DELETE: return HandlerType.DELETE;
        }
        throw new IllegalArgumentException("Not supported method: " + endpoint.getMethod());
    }

    public Javalin prepare() {
        return javalin;
    }

    public static JavalinPort server() {
        return new JavalinPort(Javalin.create());
    }
}
