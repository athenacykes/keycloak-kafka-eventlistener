package org.keycloak.events.eventlistener;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerTransaction;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.logging.Logger;

import java.util.Properties;

public class KafkaEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;
    private static Logger logger = Logger.getLogger(KafkaEventListenerProvider.class);
    private final EventListenerTransaction tx = new EventListenerTransaction(this::logAdminEvent, this::logEvent);

    public KafkaEventListenerProvider(KeycloakSession session) {
        logger.debug("Initializing KafkaEventListenerProvider...");
        this.session = session;
        this.session.getTransactionManager().enlistAfterCompletion(tx);
    }

    @Override
    public void onEvent(Event event) {
        logger.debug("Logging event: " + event.getId());
        this.logEvent(event);
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        logger.debug("Logging admin event: " + adminEvent.getId());
        logAdminEvent(adminEvent, includeRepresentation);
    }

    @Override
    public void close() {

    }

    private void kafkaSend(Properties properties, String topicName, JsonNode data){
        
        String bootstrapServer = properties.getProperty(org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG);
        logger.debug("Attempt to send to Kafka: server=" + bootstrapServer + ", topicName=" + topicName);

        Thread.currentThread().setContextClassLoader(null);
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        producer.send(new ProducerRecord<String, String>(topicName, data.toString()));

        producer.close();
    }

    private void logEvent(Event event) {
        KafkaEventListenerConfig kafkaConfig = new KafkaEventListenerConfig();
        Properties properties = kafkaConfig.getProperties();
        String topicName = properties.getProperty(KafkaEventListenerConfig.KAFKA_CONFIG_TOPIC_NAME);

        ObjectMapper eventMapper = new ObjectMapper();
        JsonNode eventJson = eventMapper.convertValue(event, JsonNode.class);

        kafkaSend(properties, topicName, eventJson);
    }

    private void logAdminEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        KafkaEventListenerConfig kafkaConfig = new KafkaEventListenerConfig();
        Properties properties = kafkaConfig.getProperties();
        String topicName = properties.getProperty(KafkaEventListenerConfig.KAFKA_CONFIG_TOPIC_NAME_ADMIN);

        ObjectMapper eventMapper = new ObjectMapper();
        JsonNode eventJson = eventMapper.convertValue(adminEvent, JsonNode.class);

        kafkaSend(properties, topicName, eventJson);
    }
}