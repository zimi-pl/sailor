package pl.zimi.http;

public interface Scheme {

    String handle(Endpoint endpoint, RequestDecoder request);

}
