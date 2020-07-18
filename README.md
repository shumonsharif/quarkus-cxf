# CXF Extension for Quarkus

SOAP (Simple Object Access Protocol) is a normalized exchange protocol based on XML, predating the era of REST services.

This extension enables you to develop web services that consume and produce SOAP payloads using [Apache CXF](http://cxf.apache.org/).

## Features
- The CXF beans are initialized lazily by Quarkus, if you want eager initialization, make sure to double-check [Quarkus Documentation](https://quarkus.io/guides/cdi-reference#eager-instantiation-of-beans). 

## Configuration

After configuring `quarkus-universe BOM`:

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.quarkus</groupId>
                <artifactId>quarkus-universe-bom</artifactId>
                <version>${insert.newest.quarkus.version.here}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

You can just configure the `quarkus-cxf` extension by adding the following dependency:

    <dependency>
        <groupId>com.github.shumonsharif</groupId>
        <artifactId>quarkus-cxf</artifactId>
    </dependency>
    
***NOTE:*** You can bootstrap a new application quickly by using [code.quarkus.io](https://code.quarkus.io) and choosing `quarkus-cxf`.


## Creating a SOAP Web service

In this example, we will create an application to manage a list of fruits.

First, let's create the `Fruit` bean as follows:

    package org.acme.cxf;

    import java.util.Objects;

    import javax.xml.bind.annotation.XmlType;

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public class Fruit {
        @XmlElement
        private String name;

        @XmlElement
        private String description;

        public Fruit() {
        }

        public Fruit(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }


        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Fruit)) {
                return false;
            }

            Fruit other = (Fruit) obj;

            return Objects.equals(other.getName(), this.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getName());
        }
    }

Now, create the `org.acme.cxf.FruitWebService` class as follows:

    package org.acme.cxf;

    import java.util.Set;

    import javax.jws.WebMethod;
    import javax.jws.WebParam;
    import javax.jws.WebService;

    @WebService
    public interface FruitWebService {

        @WebMethod
        Set<Fruit> list();

        @WebMethod
        Set<Fruit> add(Fruit fruit);

        @WebMethod
        Set<Fruit> delete(Fruit fruit);
    }


Then, create the `org.acme.cxf.FruitWebServiceImpl` class as follows:

    package org.acme.cxf;

    import java.util.Collections;
    import java.util.LinkedHashMap;
    import java.util.Set;

    import javax.jws.WebService;

    @WebService(endpointInterface = "org.acme.cxf.FruitWebService")
    public class FruitWebServiceImpl implements FruitWebService {

        private Set<Fruit> fruits = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

        public FruitWebServiceImpl() {
            fruits.add(new Fruit("Apple", "Winter fruit"));
            fruits.add(new Fruit("Pineapple", "Tropical fruit"));
        }

        @Override
        public Set<Fruit> list() {
            return fruits;
        }

        @Override
        public Set<Fruit> add(Fruit fruit) {
            fruits.add(fruit);
            return fruits;
        }

        @Override
        public Set<Fruit> delete(Fruit fruit) {
            fruits.remove(fruit);
            return fruits;
        }
    }

The implementation is pretty straightforward and you just need to define your endpoints using the `application.properties`.

    quarkus.cxf.path=/cxf
    quarkus.cxf.endpoint."/fruit".implementor=org.acme.cxf.FruitWebServiceImpl

## Creating a SOAP Client

In order to support only SOAP client, register endpoint URL and the service endpoint interface (same as the server) with configuration:

    quarkus.cxf.endpoint."/fruit".client-endpoint-url=http://localhost:8080/
    quarkus.cxf.endpoint."/fruit".service-interface=io.quarkus.cxf.deployment.test.FruitWebService

Then inject the client to use it:

    public class MySoapClient {

        @Inject
        FruitWebService clientService;

        public int getCount() {
            return clientService.count();
        }
    }


