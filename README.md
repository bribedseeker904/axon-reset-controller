# About
Provides a REST controller to reset TrackingEventProcessors and clean up JPA repositories.

# Motivation

Quite often during development and testing, we need to reset data and start fresh (probably with a different set of data) which basically means
deleting all events and projection models. The Axon Server already provides a REST endpoint to purge all events from its event store (see official
documentation). The missing piece is a simple way to also clean our databases containing projection models.

# Usage

Add a dependency in your `pom.xml`:

```xml
<dependency>
  <groupId>cz.davidstastny</groupId>
  <artifactId>axon-reset-controller</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

And then import the configuration in your Spring Boot application main class:

```java
@Import(ResetConfiguration.class)
@SpringBootApplication
public class MyApp {
  public static void main(String[] args) {
    SpringApplication.run(MyApp.class, args);
  }
}
```

Don't forget to purge your Axon Server event store (WARNING! THIS WILL ERASE ALL YOUR EVENTS):

```
DELETE http://localhost:8024/v1/devmode/purge-events
```

And finally ping the controller in a running application with a request like:

```
DELETE http://localhost:8080/reset?reason=Starting+test+case+0343
```

In case you forget to do the above purge of event store, you will still see the same data although
your database was actually erased. That is because your event store contains your events so the now-restarted
processors quickly catch up and replay all events and thus filling your database back into previous state.
