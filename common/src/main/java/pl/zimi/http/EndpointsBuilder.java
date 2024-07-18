package pl.zimi.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EndpointsBuilder {

    public static List<Endpoint> prepareEndpoints(Object service) {
        Class<?> serviceClass = service.getClass();
        String servicePart = serviceClass.getSimpleName().replace("Service", "");
        String path = servicePart.substring(0, 1).toLowerCase() + servicePart.substring(1);
        Method[] methods = serviceClass.getDeclaredMethods();
        List<Endpoint> endpoints = new ArrayList<>();
        for (Method method : methods) {
            Endpoint endpoint = prepareEndpoint(service, method, path);
            endpoints.add(endpoint);
        }
        return endpoints;
    }

    private static Endpoint prepareEndpoint(Object service, Method method, String path) {
        Function invoke = wrapMethod(service, method);
        if ("find".equals(method.getName()) || "get".equals(method.getName())) {
            return Endpoint.get()
                    .requestClass(method.getParameters()[0].getType())
                    .path("/" + path + "/{id}")
                    .handler(invoke)
                    .scheme(new IdScheme())
                    .build();
        } else if ("delete".equals(method.getName()) || "remove".equals(method.getName())) {
            return Endpoint.delete()
                    .requestClass(method.getParameters()[0].getType())
                    .path("/" + path + "/{id}")
                    .handler(invoke)
                    .scheme(new IdScheme())
                    .build();
        } else {
            return Endpoint.post()
                    .requestClass(method.getParameters()[0].getType())
                    .path("/" + path + "/" + method.getName())
                    .handler(invoke)
                    .scheme(new PostBodyScheme())
                    .build();
        }
    }

    private static Function wrapMethod(Object service, Method method) {
        return o -> {
            try {
                return method.invoke(service, o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
