package pl.zimi.example.simple;

import pl.zimi.client.HttpClient;
import pl.zimi.client.RequestBuilder;
import pl.zimi.client.Response;
import pl.zimi.example.simple.clean.CalendarService;
import pl.zimi.example.simple.clean.CalendarServiceImpl;
import pl.zimi.http.FakeHttp;
import pl.zimi.http.Server;

public class ServerExample {

    public static void main(String[] args) {
        FakeHttp fakeHttp = new FakeHttp();
        CalendarService calendarService = new CalendarServiceImpl();

        Server<?> server = fakeHttp;
        HttpClient client = fakeHttp;

        server.setupService(calendarService);

        Response response = client.handleRequest(RequestBuilder.get(server.baseUrl() + "/calendar/2024-10-13").build());

        System.out.println(response.body());
    }
}
