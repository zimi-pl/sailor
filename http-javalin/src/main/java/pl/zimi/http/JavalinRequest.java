package pl.zimi.http;

import io.javalin.http.Context;

public class JavalinRequest implements Request {

    private final Context ctx;

    JavalinRequest(Context ctx) {
        this.ctx = ctx;
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
