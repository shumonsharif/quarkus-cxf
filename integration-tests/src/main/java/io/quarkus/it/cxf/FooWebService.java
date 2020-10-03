package io.quarkus.it.cxf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface FooWebService {
    @WebMethod
    String ping(@WebParam(name = "text") String text);
}
