package com.jeffrey.whatsapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeffrey.whatsapp.entity.GreenMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Slf4j
@Service
public class GreenApi {

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

   public GreenMessage receiveNotification() throws JsonProcessingException {
      StringBuilder requestUrl = new StringBuilder();
      requestUrl
            .append("https://api.greenapi.com")
            .append("/waInstance").append("7103865679")
            .append("/receiveNotification/")
            .append("0acca0d7f38e4a2d82a331cbba76e565132de8d6aff3413faf");

      HttpHeaders headers = new HttpHeaders();
      HttpEntity<String> entity = new HttpEntity<>(null, headers);

      ResponseEntity<GreenMessage> responseJson = restTemplate.exchange(requestUrl.toString(), HttpMethod.GET, entity, GreenMessage.class);
      if (responseJson.getBody() == null) {
         return null;
      }
      GreenMessage message = responseJson.getBody();

      return message;
   }


   public void deleteNofitication(Long id) {
      StringBuilder requestUrl = new StringBuilder();
      requestUrl
            .append("https://api.greenapi.com")
            .append("/waInstance").append("7103865679")
            .append("/deleteNotification/")
            .append("0acca0d7f38e4a2d82a331cbba76e565132de8d6aff3413faf/")
            .append(id);
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      HttpEntity<String> entity = new HttpEntity<>(null, headers);
      ResponseEntity<String> response = restTemplate.exchange(requestUrl.toString(), HttpMethod.DELETE, entity, String.class);
//      System.out.println("[Delete] id: " + id);
   }
}
