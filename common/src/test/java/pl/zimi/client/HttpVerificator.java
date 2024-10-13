package pl.zimi.client;

import org.junit.jupiter.api.Test;
import pl.zimi.http.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public abstract class HttpVerificator {

    class Dto {
        String id;
    }

    class Some {
        String test;
    }

    interface SomeService {

        Dto get(String id);

        Dto other(Some some);

    }

    public class SomeServiceImpl implements SomeService {

        @Override
        public Dto get(String id) {
            Dto dto = new Dto();
            dto.id = id;
            return dto;
        }

        @Override
        public Dto other(Some some) {
            Dto dto = new Dto();
            dto.id = some.test;
            return dto;
        }
    }

    public abstract Server getServer();

    public abstract HttpClient getClient();

    @Test
    public void testGet() {
        // given
        SomeService originalService = new SomeServiceImpl();

        getServer().setupService(originalService);

        SomeService clientService = ServiceClientBuilder.client(SomeService.class, "", getClient());

        // when
        Dto dto = clientService.get("test");

        // then
        assertNotEquals(originalService, clientService);
        assertEquals("test", dto.id);
    }

    @Test
    void testOther() {
        // given
        SomeService originalService = new SomeServiceImpl();

        getServer().setupService(originalService);

        SomeService clientService = ServiceClientBuilder.client(SomeService.class, "", getClient());

        Some some = new Some();
        some.test = "value";

        // when
        Dto dto = clientService.other(some);

        // then
        assertNotEquals(originalService, clientService);
        assertEquals(dto.id, "value");
    }

}