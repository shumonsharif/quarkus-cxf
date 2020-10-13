package io.quarkus.it.cxf;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/rest")
public class RestResource {
    @Inject
    public GreetingClientWebService greetingWS;// = CDI.current().select(GreetingWebService.class).get();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return greetingWS.reply("foo");
    }
}
