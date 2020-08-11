# api-sdk-java
API wrapper SDK written in Java

## Maven Integration

In order to use 'api-sdk-java' in your project, you need to add the following 'repository' to the 'repositories' section of your project's pom:
```xml
    ...
    <repositories>
        ...
        <repository>
            <id>addradio-public-mvn-repo</id>
            <name>AddRadio Public Maven Repository</name>
            <url>http://mvn-repo.dev.addradio.net/mvn-repo/releases</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
        ...
    </repositories>
    ...
```
Then you also need to add the following dependency:
```xml
        <dependency>
            <groupId>io.ybrid</groupId>
            <artifactId>api-sdk-java</artifactId>
            <version>1.0.1-SNAPSHOT</version>
        </dependency>
```

## Getting started
As this package provides low level access applications only use the Alias and Session classes directly.
Every other access is normally done using the Player package.

A simple example of using the Alias and Session classes as often seen in applications is shown below:

```java
import io.ybrid.api.*;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

class Client {
    void run() throws IOException {
        /* URL to the Bouquet to connect to */
        URL url = new URL("https://stagecast.ybrid.io/adaptive-demo");

        /* Create an Alias object from the URL.*/
        Alias alias = new Alias(url);

        /* create an unconnected session */
        Session session = alias.createSession();

        /* Connect the session to the server. */
        session.connect();

        /* Run a player using this session. */
        runPlayer(session);

        /* After the player finished close the session. */
        session.close();
    }

    /* ... */
}
```

## Copyright
Copyright (c) 2019 nacamar GmbH, Germany. See [MIT License](LICENSE) for details.
