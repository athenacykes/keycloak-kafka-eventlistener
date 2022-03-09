package org.keycloak.events.eventlistener;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.fasterxml.jackson.databind.JsonNode;

import org.jboss.logging.Logger;

import java.util.Properties;

public class KafkaEventSender implements Runnable {

    private static Logger logger = Logger.getLogger(KafkaEventSender.class);
    private final Properties properties;
    private final String topicName;
    private final JsonNode data;

    public KafkaEventSender(Properties properties, String topicName, JsonNode data){
        this.properties = properties;
        this.topicName = topicName;
        this.data = data;
    }

    @Override
    public void run() {
        logger.debug("Starting KafkaEventSender thread.");
        try {
            kafkaSend(this.properties, this.topicName, this.data);
        } catch (Exception e) {
            logger.error("Uncaught error in KafkaEventSender thread: ", e);
        }

    }

    private void kafkaSend(Properties properties, String topicName, JsonNode data){
        String bootstrapServer = properties.getProperty(org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG);
        logger.debug("Attempt to send to Kafka: server=" + bootstrapServer + ", topicName=" + topicName);

        Thread.currentThread().setContextClassLoader(null);
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        try{
            logger.debug("Producer send to: server=" + bootstrapServer + ", topicName=" + topicName);
            producer.send(new ProducerRecord<String, String>(topicName, data.toString()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            producer.close();
        }        
    }
}
