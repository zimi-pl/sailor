package pl.zimi.http;

public interface Server {

    Server setupEndpoint(Endpoint endpoint);

    Response handleRequest(Request request);

}
