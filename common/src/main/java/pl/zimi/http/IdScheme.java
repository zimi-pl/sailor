package pl.zimi.http;

import com.google.gson.Gson;

public class IdScheme implements Scheme {
    @Override
    public String handle(Endpoint endpoint, Request request) {
        Gson gson = new Gson();
        String requestArgument = request.pathParam("id");
        Object result = endpoint.getHandler().apply(requestArgument);
        return gson.toJson(result);
    }
}
