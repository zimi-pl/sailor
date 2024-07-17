package pl.zimi.http;

import com.google.gson.Gson;
import pl.zimi.repository.manipulation.Manipulator;

import java.lang.reflect.InvocationTargetException;

public class PathBasedScheme implements Scheme {
    @Override
    public String handle(Endpoint endpoint, Request request) {
        try {
            Gson gson = new Gson();
            Object requestArgument = endpoint.getRequestClass().getDeclaredConstructor().newInstance();
            for (Mapping mapping : endpoint.getMappings()) {
                String value = request.pathParam(mapping.getVariableName());
                Manipulator.set(requestArgument, mapping.getDescriptor(), value);
            }
            Object result = endpoint.getHandler().apply(requestArgument);
            return gson.toJson(result);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
