package io.quarkus.it.cxf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GreetingClientTest {
    @Test
    void testSoapEndpoint() {
        GreetingClient client = new GreetingClient();
        Assertions.assertNotNull(client.fooService);
        String res = client.checkPing();
        Assertions.assertTrue(res.contains("bar"));
    }
}
