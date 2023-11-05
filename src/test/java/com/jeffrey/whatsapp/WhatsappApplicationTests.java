package com.jeffrey.whatsapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeffrey.whatsapp.utils.GreenApiUtils;
import com.jeffrey.whatsapp.utils.KafkaUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Map;
import java.util.Scanner;

@SpringBootTest
class WhatsappApplicationTests {
   @Autowired
   GreenApiUtils greenApiUtils;
   @Autowired
   KafkaUtils kafkaUtils;

   @Test
   void contextLoads() {
      System.out.println("Hello World");
   }

   @Test
   void testGreenGet() throws JsonProcessingException {
      StringBuilder requestUrl = new StringBuilder();
      requestUrl
            .append("https://api.greenapi.com")
            .append("/waInstance").append("7103865679")
            .append("/receiveNotification/")
            .append("0acca0d7f38e4a2d82a331cbba76e565132de8d6aff3413faf");
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
//      headers.set("Authorization", "Bearer YOUR_TOKEN");  // For example, setting an authorization header
//      headers.set("Custom-Header", "CustomValue");        // Setting a custom header

      HttpEntity<String> entity = new HttpEntity<>(null, headers);
      ResponseEntity<String> response = restTemplate.exchange(requestUrl.toString(), HttpMethod.GET, entity, String.class);

      // Print the response body
      System.out.println(response.getBody());

      String jsonResponse = response.getBody();
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> map = mapper.readValue(jsonResponse, Map.class);

      Map<String, Object> bodyMap = (Map<String, Object>) map.get("body");
      Map<String, Object> messageMap = (Map<String, Object>) bodyMap.get("messageData");
      Map<String, String > textMessageMap = (Map<String, String>) messageMap.get("textMessageData");


      String text = textMessageMap.get("textMessage");
      String webhook = (String) bodyMap.get("typeWebhook");
      int id = (int) map.get("receiptId");

      System.out.println("Type: " + webhook + ", message: " + text + ", id: " + id);

   }

   @Test
   void testGreenDelete() {
      StringBuilder requestUrl = new StringBuilder();
      requestUrl
            .append("https://api.greenapi.com")
            .append("/waInstance").append("7103865679")
            .append("/deleteNotification/")
            .append("0acca0d7f38e4a2d82a331cbba76e565132de8d6aff3413faf/")
            .append("31");
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();

      HttpEntity<String> entity = new HttpEntity<>(null, headers);
      ResponseEntity<String> response = restTemplate.exchange(requestUrl.toString(), HttpMethod.DELETE, entity, String.class);

   }

   @Test
   void testClearQueue() throws JsonProcessingException {
      greenApiUtils.clearQueue();
   }

   @Test
   void testWebsocket() throws Exception {

      WebSocketClient webSocketClient = new StandardWebSocketClient();
      WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

      WebSocketSession session = webSocketClient.doHandshake(
               new MyHandler(), headers,
               URI.create("ws://192.168.1.73:8080/ws/a032446986074dd7f9d86bd1d982ac0d443991db?api_key=&llm_model=gpt-3.5-turbo-16k")).get();


      session.sendMessage(new TextMessage("Terminal"));
      session.sendMessage(new TextMessage("1"));

      Scanner scanner = new Scanner(System.in);
   }

   private static class MyHandler extends TextWebSocketHandler {
      @Override
      public void handleTextMessage(WebSocketSession session, TextMessage message) {
         System.out.println("Received message: " + message.getPayload());
      }
   }

   @Test
   public void testKafka() {
      kafkaUtils.sendMessage("TestMessage", 1, "Jeffrey", "Hello World");
   }

}
