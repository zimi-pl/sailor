package pl.zimi.http;

import com.google.gson.Gson;
import pl.zimi.repository.manipulation.Manipulator;

public class PostBodyScheme implements Scheme {
    @Override
    public String handle(Endpoint endpoint, Request request) {
        Gson gson = new Gson();
        Object requestArgument = gson.fromJson(request.body(), endpoint.getRequestClass());
        if (endpoint.getMappings() != null) {
            for (Mapping mapping : endpoint.getMappings()) {
                String value = request.pathParam(mapping.getVariableName());
                Manipulator.set(requestArgument, mapping.getDescriptor(), value);
            }
        }
        Object result = endpoint.getHandler().apply(requestArgument);
        return gson.toJson(result);
    }
}
