package pl.zimi.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Server {

    public static List<Endpoint> prepareEndpoints(Object service) {
        Class<?> serviceClass = service.getClass();
        String servicePart = serviceClass.getSimpleName().replace("Service", "");
        String path = servicePart.substring(0, 1).toLowerCase() + servicePart.substring(1);
        Method[] methods = serviceClass.getDeclaredMethods();
        List<Endpoint> endpoints = new ArrayList<>();
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
                    .scheme(new PostBodyScheme())
                    .build();
            endpoints.add(endpoint);
        }
        return endpoints;
    }

}
