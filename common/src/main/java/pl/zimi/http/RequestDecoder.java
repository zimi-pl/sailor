package pl.zimi.http;

public interface RequestDecoder {

    String fullPath();

    String pathParam(String variable);

    String body();

}
