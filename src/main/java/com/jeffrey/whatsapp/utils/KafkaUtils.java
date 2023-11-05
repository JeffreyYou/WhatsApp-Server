package com.jeffrey.whatsapp.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaUtils {
   @Autowired
   KafkaTemplate kafkaTemplate;

   CompletableFuture[] futures = new CompletableFuture[1000];
   public void sendMessage(String topic, int partition, String key, String message) {

//      CompletableFuture future = kafkaTemplate.send(topic, partition, key, message);
      CompletableFuture future = kafkaTemplate.send(topic, key, message);

      future.join();
   }

   @KafkaListener(topics = "TestMessage", groupId = "group1")
   public void getMessages(ConsumerRecord<String, String> record) {
      String topic = record.topic();
      String key = record.key();
      String value = record.value();
      System.out.println("group1: key: " + key + ", value: " + value + ", topic: " + topic);
   }

   @KafkaListener(groupId = "group2",topicPartitions = {
         @TopicPartition(topic = "TestMessage", partitionOffsets = {
            @PartitionOffset(partition = "0", initialOffset = "0")
      })
   })
   public void getAllMessages(ConsumerRecord<String, String> record) {
      String topic = record.topic();
      String key = record.key();
      String value = record.value();
      System.out.println("group2: key: " + key + ", value: " + value + ", topic: " + topic);
   }

}
