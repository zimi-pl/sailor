package pl.zimi.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Response {

    private final String body;

    public Response(String body) {
        this.body = body;
    }

    public JsonElement json() {
        return JsonParser.parseString(body);
    }
}
