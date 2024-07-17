package pl.zimi.http;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Handler;
import pl.zimi.repository.manipulation.Manipulator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class JavalinPort {

    static void setupEndpoint(Javalin javalin, Endpoint endpoint) {
        System.out.println(endpoint);
        Gson gson = new Gson();
        if (endpoint.getMethod() == HttpMethod.GET) {
            Handler handler = ctx -> {
                Object request = endpoint.getRequestClass().getDeclaredConstructor().newInstance();
                for (Mapping mapping : endpoint.getMappings()) {
                    String value = ctx.pathParam(mapping.getVariableName());
                    Manipulator.set(request, mapping.getDescriptor(), value);
                }
                Object result = endpoint.getHandler().apply(request);
                String json = gson.toJson(result);
                ctx.contentType(ContentType.APPLICATION_JSON).result(json);
            };
            javalin.get(endpoint.getPath(), handler);
        } else if (endpoint.getMethod() == HttpMethod.DELETE) {

        } else if (endpoint.getMethod() == HttpMethod.PUT) {

        } else if (endpoint.getMethod() == HttpMethod.POST) {
            Handler handler = ctx -> {
                Object request = gson.fromJson(ctx.body(), endpoint.getRequestClass());
                if (endpoint.getMappings() != null) {
                    for (Mapping mapping : endpoint.getMappings()) {
                        String value = ctx.pathParam(mapping.getVariableName());
                        Manipulator.set(request, mapping.getDescriptor(), value);
                    }
                }
                Object result = endpoint.getHandler().apply(request);
                String json = gson.toJson(result);
                ctx.contentType(ContentType.APPLICATION_JSON).result(json);
            };
            javalin.post(endpoint.getPath(), handler);
        }
    }

    public static void setupEndpoints(Javalin javalin, Object service) {
        Class<?> serviceClass = service.getClass();
        String servicePart = serviceClass.getSimpleName().replace("Service", "");
        String path = servicePart.substring(0, 1).toLowerCase() + servicePart.substring(1);
        Method[] methods = serviceClass.getDeclaredMethods();
        for (Method method : methods) {
            Function invoke = o -> {
                try {
                    return method.invoke(service, o);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            Endpoint endpoint = Endpoint.post()
                    .requestClass(method.getParameters()[0].getType())
                    .path("/" + path + "/" + method.getName())
                    .handler(invoke)
                    .build();
            setupEndpoint(javalin, endpoint);
        }

    }
    public static class JavalinServer {

        private Javalin javalin;

        public JavalinServer(Javalin javalin) {
            this.javalin = javalin;
        }

        public JavalinServer setupService(Object service) {
            setupEndpoints(javalin, service);
            return this;
        }

        public JavalinServer setupEndpoint(Endpoint endpoint) {
            JavalinPort.setupEndpoint(javalin, endpoint);
            return this;
        }

        public Javalin prepare() {
            return javalin;
        }
    }
    public static JavalinServer server() {
        return new JavalinServer(Javalin.create());
    }
}
