package com.uidai.sandbox.config;

import com.uidai.sandbox.dto.RiskEvaluationRequestedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    public static final String TOPIC = "Risk_Evaluation_Requested";

    @Bean
    public NewTopic riskEvaluationTopic() {
        return new NewTopic(TOPIC, 3, (short) 1);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                "bootstrap.servers",
                "localhost:9092"
        );

        props.put(
                "key.serializer",
                StringSerializer.class
        );

        props.put(
                "value.serializer",
                JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, RiskEvaluationRequestedEvent>
    consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                "bootstrap.servers",
                "localhost:9092"
        );

        props.put(
                "group.id",
                "risk-evaluation-worker"
        );

        props.put(
                "key.deserializer",
                StringDeserializer.class
        );

        /*
         * IMPORTANT:
         *
         * Do NOT configure JsonDeserializer using both
         * properties and setters.
         *
         * We configure the deserializer directly below.
         */

        JsonDeserializer<RiskEvaluationRequestedEvent> deserializer =
                new JsonDeserializer<>(RiskEvaluationRequestedEvent.class);

        deserializer.addTrustedPackages("com.uidai.sandbox.dto");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RiskEvaluationRequestedEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, RiskEvaluationRequestedEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, RiskEvaluationRequestedEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        /*
         * Two Kafka consumer threads.
         * This allows multiple partitions to be processed concurrently.
         */
        factory.setConcurrency(2);

        return factory;
    }
}