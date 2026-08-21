//package com.mo.configuration.kafka;
//
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaConfig {
//
//    // ---------- Producer ----------
//    @Bean
//    ProducerFactory<String, Object> producerFactory(KafkaProperties props) {
//        Map<String, Object> config = new HashMap<>(props.buildProducerProperties());
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
//        return new DefaultKafkaProducerFactory<>(config);
//    }
//
//    @Bean
//    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
//        return new KafkaTemplate<>(pf);
//    }
//
//    // ---------- Consumer ----------
//    @Bean
//    ConsumerFactory<String, Object> consumerFactory(KafkaProperties props) {
//        Map<String, Object> config = new HashMap<>(props.buildConsumerProperties());
//        JsonDeserializer<Object> valueDeserializer = new JsonDeserializer<>();
//        valueDeserializer.addTrustedPackages("*");
//        valueDeserializer.setUseTypeMapperForKey(false);
//        valueDeserializer.setRemoveTypeHeaders(false); // on garde les headers
//        return new DefaultKafkaConsumerFactory<>(
//                config,
//                new StringDeserializer(),
//                valueDeserializer
//        );
//    }
//
//    @Bean
//    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
//             ConsumerFactory<String, Object> cf) {
//        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(cf);
//        return factory;
//    }
//}