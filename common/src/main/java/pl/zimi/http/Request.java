package pl.zimi.http;

public interface Request {

    String pathParam(String variable);

    String body();
}
