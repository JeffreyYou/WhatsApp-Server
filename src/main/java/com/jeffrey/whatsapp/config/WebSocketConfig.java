package com.jeffrey.whatsapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class WebSocketConfig {
   @Bean
   public WebSocketClient webSocketClient() {
      return new StandardWebSocketClient();
   }

   @Bean
   public ConcurrentHashMap<String, WebSocketSession> concurrentHashMap() {
      return new ConcurrentHashMap<>();
   }


}
