package pl.zimi.http;

import io.javalin.http.Context;

public class JavalinRequest implements RequestDecoder {

    private final Context ctx;

    JavalinRequest(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public String fullPath() {
        return null;
    }

    @Override
    public String pathParam(String variable) {
        return ctx.pathParam(variable);
    }

    @Override
    public String body() {
        return ctx.body();
    }
}
