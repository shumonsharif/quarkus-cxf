package io.quarkus.cxf;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

public class AbstractCxfClientProducer {

    public Object loadCxfClient(String sei, String endpointAddress, String wsdlUrl, String soapBinding) {
        Class<?> seiClass;
        try {
            seiClass = Class.forName(sei);
        } catch (ClassNotFoundException e) {
            return null;
        }
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(seiClass);
        factory.setAddress(endpointAddress);
        factory.setBindingId(soapBinding);
        if (wsdlUrl != null && !wsdlUrl.isEmpty()) {
            factory.setWsdlURL(wsdlUrl);
        }
        return factory.create();
    }
}
