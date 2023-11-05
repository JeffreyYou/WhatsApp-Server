package com.jeffrey.whatsapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jeffrey.whatsapp.entity.GreenMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class GreenApiUtils {

   @Autowired
   private RestTemplate restTemplate;

   public void clearQueue() throws JsonProcessingException {
      System.out.println("-----------------------------------------------------Service Started------------------------------------------------------------");
      log.info("Cleaning existing messages...");
      GreenMessage data = receiveNotification();
      while (data != null) {
         deleteNofitication(data.getReceiptId());
         data = receiveNotification();
      }
      log.info("Message queue cleared");
   }

   public void sendMessageToUser(String message, String recipient) {
      String url = generateUrl("send", 0L);

      Map<String, String> payload = new HashMap<>();
      payload.put("chatId", recipient);
      payload.put("message", message);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

      // Making the POST request
      ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
//      System.out.println("Response: " + response.getBody());
   }
   public GreenMessage receiveNotification() throws JsonProcessingException {
      String url = generateUrl("receive", 0L);

      HttpHeaders headers = new HttpHeaders();
      HttpEntity<String> entity = new HttpEntity<>(null, headers);

      ResponseEntity<GreenMessage> responseJson = restTemplate.exchange(url, HttpMethod.GET, entity, GreenMessage.class);
      GreenMessage message = responseJson.getBody();
      if (message == null) {
         return null;
      }

      return message;
   }


   public void deleteNofitication(Long id) {
      String url = generateUrl("delete", id);
      HttpHeaders headers = new HttpHeaders();
      HttpEntity<String> entity = new HttpEntity<>(null, headers);
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
   }

   private String generateUrl(String type, Long id) {
      String domain = "https://api.greenapi.com/waInstance";
      String instanceId = "7103865679/";
      String method = "";
      String token = "0acca0d7f38e4a2d82a331cbba76e565132de8d6aff3413faf";
      if (type.equals("receive")) {
         method = "receiveNotification/";
      }
      if (type.equals("delete")) {
         method = "deleteNotification/";
         token = token + "/" + id;
      }
      if (type.equals("send")) {
         method = "SendMessage/";
      }
      StringBuilder requestUrl = new StringBuilder();
      requestUrl
            .append(domain)
            .append(instanceId)
            .append(method)
            .append(token);

      return requestUrl.toString();
   }
}
