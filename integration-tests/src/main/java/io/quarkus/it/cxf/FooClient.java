package io.quarkus.it.cxf;

import javax.inject.Inject;

public class FooClient {
    @Inject
    public FooWebService fooService;

    public String checkPing()
    {
        String result = "";
        result = fooService.ping("bar");

        return result;
    }
}
