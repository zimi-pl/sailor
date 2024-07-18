package pl.zimi.http;

import com.google.gson.Gson;

public class NoArgScheme implements Scheme {
    @Override
    public String handle(Endpoint endpoint, Request request) {
        Gson gson = new Gson();
        return gson.toJson(endpoint.getHandler().apply(null));
    }
}
