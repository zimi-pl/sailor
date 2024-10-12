package pl.zimi.http;

import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import pl.zimi.client.HttpClient;
import pl.zimi.client.Request;
import pl.zimi.client.Response;

public class JavalinServer implements Server<Javalin>, HttpClient {
    private Javalin javalin;
    private JavalinServer(Javalin javalin) {
        this.javalin = javalin;
    }

    @Override
    public JavalinServer setupEndpoint(Endpoint endpoint) {
        System.out.println(endpoint);
        Handler handler = ctx -> {
            RequestDecoder request = new JavalinRequest(ctx);
            String json = endpoint.getScheme().handle(endpoint, request);
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

    @Override
    public void start() {
        javalin.start(7070);
    }

    @Override
    public Response handleRequest(Request request) {
        return null;
    }

    public static JavalinServer server() {
        return new JavalinServer(Javalin.create());
    }
}
