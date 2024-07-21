package pl.zimi.http;

import com.google.gson.Gson;
import pl.zimi.repository.manipulation.Manipulator;

public class IdScheme implements Scheme {
    @Override
    public String handle(Endpoint endpoint, RequestDecoder request) {
        Gson gson = new Gson();
        Object requestArgument;
        String id = request.pathParam("id");
        if (endpoint.getRequestClass().getPackageName().startsWith("java.lang")) {
            requestArgument = cast(endpoint.getRequestClass(), id);
        } else {
            Class<?> argClass = Manipulator.detectSingleArgumentClass(endpoint.getRequestClass());
            requestArgument = Manipulator.singleArgConstructor(endpoint.getRequestClass(), cast(argClass, id));
        }
        Object result = endpoint.getHandler().apply(requestArgument);
        return gson.toJson(result);
    }

    private Object cast(Class<?> clazz, String value) {
        if (String.class.equals(clazz)) {
            return value;
        } else if (Long.class.equals(clazz)) {
            return Long.parseLong(value);
        } else if (Integer.class.equals(clazz)) {
            return Integer.parseInt(value);
        } else {
            throw new IllegalArgumentException("Not supported class for casting: " + clazz.getName());
        }
    }

}
