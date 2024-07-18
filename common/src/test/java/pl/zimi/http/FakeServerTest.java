package pl.zimi.http;

import org.junit.jupiter.api.Test;

class FakeServerTest {

    @Test
    public void test() {
        ServerVerificator.assertThese(FakeServer::new);
    }

}