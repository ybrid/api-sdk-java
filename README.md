# api-sdk-java
API wrapper SDK written in Java

## Important Note

For reasons of the experimental status of the Ybrid® "v2" protocol it has been disabled by default.

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
            <version>1.2.0</version>
        </dependency>
```

## Getting started
As this package provides low level access applications only use the Alias and Session classes directly.
Every other access is normally done using the Player package.

A simple example of using the Alias and Session classes as often seen in applications is shown below:

```java
import io.ybrid.api.*;
import io.ybrid.api.session.Session;

import java.io.IOException;
import java.net.URI;

class Client {
    void run() throws IOException {
        /* URI to the Bouquet to connect to */
        final URI uri = new URI("https://stagecast.ybrid.io/adaptive-demo");

        /* Create an MediaEndpoint object from the URI. */
        final MediaEndpoint mediaEndpoint = new MediaEndpoint(uri);

        /* create an unconnected session */
        final Session session = mediaEndpoint.createSession();

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
Copyright (c) 2019-2021 nacamar GmbH, Germany. See [MIT License](LICENSE) for details.
