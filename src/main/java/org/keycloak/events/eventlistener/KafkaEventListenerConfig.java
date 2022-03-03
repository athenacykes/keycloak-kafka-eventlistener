package org.keycloak.events.eventlistener;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class KafkaEventListenerConfig {

    private Properties properties;
    public static final String KAFKA_CONFIG_TOPIC_NAME = "kafka.config.topic.name";
    public static final String KAFKA_CONFIG_TOPIC_NAME_ADMIN = "kafka.config.topic.name.admin";

    public KafkaEventListenerConfig(){
        Properties props = new Properties();

        try {
            String jbossServerConfigDir = System.getProperty("jboss.server.config.dir");
            if (jbossServerConfigDir != null) {
                File file = new File(jbossServerConfigDir, "kafka.properties");
                if (file.isFile()) {
                    try (FileInputStream is = new FileInputStream(file)) {
                        props.load(is);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.properties = props;
    }

    public Properties getProperties(){
        return properties;
    }
    
}
