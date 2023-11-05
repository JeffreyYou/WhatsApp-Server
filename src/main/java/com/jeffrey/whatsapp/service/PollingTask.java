package com.jeffrey.whatsapp.service;

import com.jeffrey.whatsapp.entity.GreenMessage;
import com.jeffrey.whatsapp.utils.GreenApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PollingTask {

   @Autowired
   private GreenApiUtils greenApiUtils;
   @Autowired
   private HandleRequest handleRequest;

//   @PostConstruct
   @Scheduled(fixedRate = 1000)
   public void continuousPolling() throws Exception {

      greenApiUtils.clearQueue();
      GreenMessage data = null;
      while (true) {
         try {
            data = greenApiUtils.receiveNotification();
            if (data != null) {
               if (data.getBody().getTypeWebhook().equals("incomingMessageReceived")) {
                  handleRequest.handleRequest(data);
                  greenApiUtils.deleteNofitication(data.getReceiptId());
               } else {
                  greenApiUtils.deleteNofitication(data.getReceiptId());
               }

            } else {
//               Thread.sleep(1000);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}
