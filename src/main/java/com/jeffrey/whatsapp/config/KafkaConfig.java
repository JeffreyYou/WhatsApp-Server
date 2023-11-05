package com.jeffrey.whatsapp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

   @Bean
   public NewTopic topic1() {
      return TopicBuilder.name("Incoming_Messages")
            .partitions(3)
            .replicas(2)
            .build();
   }
}
