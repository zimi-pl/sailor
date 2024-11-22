package pl.zimi.example.simple;

import pl.zimi.client.HttpClient;
import pl.zimi.client.ServiceClientBuilder;
import pl.zimi.example.simple.clean.CalendarService;
import pl.zimi.example.simple.clean.DayInfo;
import pl.zimi.http.Endpoint;
import pl.zimi.http.FakeHttp;
import pl.zimi.http.IdScheme;
import pl.zimi.http.Server;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;

public class ClientExample {

    public static void main(String[] args) {
        FakeHttp fakeHttp = new FakeHttp();

        Server<?> server = fakeHttp;
        HttpClient client = fakeHttp;

        Endpoint endpoint = Endpoint.get()
                .requestClass(String.class)
                .path("/calendar/{id}")
                .handler((Object arg) -> getDayInfo((String)arg))
                .scheme(new IdScheme())
                .build();

        server.setupEndpoint(endpoint);

        CalendarService calendarService = ServiceClientBuilder.client(CalendarService.class, server.baseUrl(), client);

        DayInfo dayInfo = calendarService.get("2024-10-13");

        System.out.println(dayInfo);
    }

    private static DayInfo getDayInfo(String date) {
        LocalDate localDate = LocalDate.parse(date);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return new DayInfo(localDate, dayOfWeek.name(), Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(dayOfWeek));
    }
}
