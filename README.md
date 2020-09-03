# CXF Extension for Quarkus

[![Build](https://github.com/shumonsharif/quarkus-cxf/workflows/build/badge.svg)](https://github.com/shumonsharif/quarkus-cxf/actions?query=workflow%3Abuild)
[![Nexus](https://img.shields.io/nexus/r/com.github.shumonsharif/quarkus-cxf?nexusVersion=2&server=https%3A%2F%2Foss.sonatype.org)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

SOAP (Simple Object Access Protocol) is a normalized exchange protocol based on XML, predating the era of REST services.

This extension enables you to develop web services that consume and produce SOAP payloads using the [Apache CXF](http://cxf.apache.org/) libraries.

  - [Credits](#credits)
  - [Configuration](#configuration)
  - [Creating a SOAP Web service](#creating-a-soap-web-service)
  - [Creating a SOAP Client](#creating-a-soap-client)
  - [Native Mode Limitations](#native-mode-limitations)

## Credits

This project builds upon a considerable amount of work done as part of a separate effort. Please review [CREDITS.md](https://github.com/shumonsharif/quarkus-cxf/blob/master/CREDITS.md) for more details.

## Configuration

After configuring `quarkus BOM`:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-bom</artifactId>
            <version>${insert.newest.quarkus.version.here}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

You can just configure the `quarkus-cxf` extension by adding the following dependency:

```xml
<dependency>
    <groupId>com.github.shumonsharif</groupId>
    <artifactId>quarkus-cxf</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
<!--
***NOTE:*** You can bootstrap a new application quickly by using [code.quarkus.io](https://code.quarkus.io) and choosing `quarkus-cxf`.
-->

## Creating a SOAP Web service

In this example, we will create an application to manage a list of fruits.

First, let's create the `Fruit` bean as follows:

```java
package org.acme.cxf;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Fruit")
@XmlRootElement
public class Fruit {

    private String name;

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

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    @XmlElement
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
```

Now, create the `org.acme.cxf.FruitWebService` class as follows:

```java
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
```

Then, create the `org.acme.cxf.FruitWebServiceImpl` class as follows:

```java
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
```

The implementation is pretty straightforward and you just need to define your endpoints using the `application.properties`.

```properties
quarkus.cxf.path=/cxf
quarkus.cxf.endpoint."/fruit".implementor=org.acme.cxf.FruitWebServiceImpl
```

The following sample curl command can be used to test your Fruit service.

```
curl -X POST "http://localhost:8080/cxf/fruit" \
 -H 'Content-Type: text/xml' \
 -H 'SOAPAction:' \
 -d '
 <soapenv:Envelope 
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:cxf="http://cxf.acme.org/">
   <soapenv:Header/>
   <soapenv:Body>
      <cxf:list/>
   </soapenv:Body>
</soapenv:Envelope>'
```

## Creating a SOAP Client

In order to support only SOAP client, register endpoint URL and the service endpoint interface (same as the server) with configuration:

```properties
quarkus.cxf.endpoint."/fruit".client-endpoint-url=http://localhost:8080/
quarkus.cxf.endpoint."/fruit".service-interface=org.acme.cxf.FruitWebService
```

Then inject the client to use it:

```java
public class MySoapClient {

    @Inject
    FruitWebService clientService;

    public int getCount() {
        return clientService.count();
    }
}
```

## Native Mode Limitations

- Native mode is currently not fully functional, though work is in progress to make it as such.
- Please feel free to submit pull requests; to execute native integration tests, you can use:
    ```
    mvn integration-test -pl :quarkus-cxf-integration-tests -Dnative
    ```