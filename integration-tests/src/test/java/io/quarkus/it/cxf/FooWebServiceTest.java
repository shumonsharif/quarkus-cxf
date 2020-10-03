package io.quarkus.it.cxf;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

@QuarkusTest
public class FooWebServiceTest {
    @Test
    void testSoapEndpoint() {
        FooClient client = new FooClient();
        Assertions.assertNotNull(client.fooService);
        String res = client.checkPing();
        Assertions.assertTrue(res.contains("get success"));
    }
}
