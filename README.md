# keycloak-kafka-eventlistener

Output Keycloak Events and Admin Events to a Kafka topic.

Based on Keycloak 15.0.2+ / RH-SSO 7.5.0+

# How to use the plugin

1. Build the plugin
```
mvn clean package
```

2. Put `target/keycloak-kafka-eventlistener-1.0.jar` to `$KEYCLOAK_HOME/providers`

3. Put kafka client and module.xml to `$KEYCLOAK_HOME/modules/system/layers/keycloak/org/apache/kafka/clients/main`

4. Add the following line in section `<dependency>` of `$KEYCLOAK_HOME/modules/system/layers/keycloak/org/keycloak/keycloak-services/main/module.xml`:

```
<module name="org.apache.kafka.clients" services="import"/>
```

5. Create `$KEYCLOAK_HOME/standalone/configurations/kafka.properties` with following content:

```
bootstrap.servers={KAFKA_HOST}:{PORT}
kafka.config.topic.name={KAFKA_TOPIC}
kafka.config.topic.name.admin={KAFKA_TOPIC_ADMIN}
```

6. Restart keycloak

7. On Events settings, select kafka-event-listener, select some event types, and save