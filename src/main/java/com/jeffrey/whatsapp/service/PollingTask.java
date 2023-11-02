package com.jeffrey.whatsapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jeffrey.whatsapp.entity.GreenMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PollingTask {

   @Autowired
   private GreenApi greenApi;
   @Autowired
   private HandleRequest handleRequest;

//   @PostConstruct
   public void continuousPolling() throws JsonProcessingException {

      greenApi.clearQueue();
      GreenMessage data = null;
      while (true) {
         try {
            data = greenApi.receiveNotification();
            if (data != null) {
               if (data.getBody().getTypeWebhook().equals("incomingMessageReceived")) {
                  handleRequest.handleRequest(data);
                  greenApi.deleteNofitication(data.getReceiptId());
               } else {
                  greenApi.deleteNofitication(data.getReceiptId());
               }
            } else {
               Thread.sleep(1000);
            }
         } catch (JsonProcessingException | InterruptedException e) {
            e.printStackTrace();
         }
      }
   }
}
