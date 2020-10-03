package io.quarkus.it.cxf;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

public class GreetingClient {
    // Commented out @Inject annotation to allow tests to pass
    //@Inject
    public GreetingWebService fooService = CDI.current().select(GreetingWebService.class).get();

    public String checkPing()
    {
        String result = "";
        result = fooService.reply("bar");

        return result;
    }
}
