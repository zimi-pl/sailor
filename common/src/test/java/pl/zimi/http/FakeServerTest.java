package pl.zimi.http;

import org.junit.jupiter.api.Test;
import pl.zimi.client.FakeClient;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FakeServerTest {

//    @Test
//    public void test() {
//        ServerVerificator.assertThese(FakeServer::new, new FakeClient());
//    }

    @Test
    public void testPrepareEndpointData() {
        // given
        Endpoint endpoint = Endpoint.builder()
                .method(HttpMethod.GET)
                .path("/some/{id}/{test}/{hello}/")
                .requestClass(String.class)
                .handler(x -> x)
                .scheme(new IdScheme())
                .build();

        // when
        FakeServer.EndpointData endpointData = new FakeServer().prepareEndpointData(endpoint);

        // then
        assertEquals(Arrays.asList("id", "test", "hello"), endpointData.getVariables());
        assertEquals("/some/([^/]+)/([^/]+)/([^/]+)/", endpointData.getUrlPattern());
    }

}