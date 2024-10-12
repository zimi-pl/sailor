package pl.zimi.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@WireMockTest
public class OkhttpClientTest {

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

    @Test
    void test(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(get(urlEqualTo("/some/7")).willReturn(aResponse().withBody("{\"id\": \"7\"}")));

        SomeService someService = ServiceClientBuilder.client(SomeService.class, wmRuntimeInfo.getHttpBaseUrl(), new OkhttpClient());

        // when
        Dto dto = someService.get("7");

        // then
        assertEquals(dto.id, "7");
    }

    @Test
    void testOther(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(post(urlEqualTo("/some/other"))
                .withRequestBody(equalToJson("{\"test\":  \"value\"}"))
                .willReturn(aResponse().withBody("{\"id\": \"value\"}")));

        SomeService someService = ServiceClientBuilder.client(SomeService.class, wmRuntimeInfo.getHttpBaseUrl(), new OkhttpClient());
        Some some = new Some();
        some.test = "value";

        // when
        Dto dto = someService.other(some);

        // then
        assertEquals(dto.id, "value");
    }
}
