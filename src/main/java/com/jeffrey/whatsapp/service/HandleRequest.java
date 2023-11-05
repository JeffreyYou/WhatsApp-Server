package com.jeffrey.whatsapp.service;

import com.jeffrey.whatsapp.entity.GreenMessage;
import com.jeffrey.whatsapp.entity.WhatsappUser;
import com.jeffrey.whatsapp.utils.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


@Slf4j
@Service
public class HandleRequest {


   @Autowired
   WebSocketService webSocketService;
   @Autowired
   WebSocketUtils webSocketUtils;
      public void handleRequest(GreenMessage data) {

         WhatsappUser whatsappUser = webSocketUtils.createUserIfNotExist(data);
         String message = data.getBody().getMessageData().getTextMessageData().getTextMessage();
         try {
//            WebSocketSession session = webSocketService.establishSession(whatsappUser);
            WebSocketSession session = webSocketUtils.createSessionIfNotExist(whatsappUser);
            session.sendMessage(new TextMessage(message));
            log.info("[User]: " + message);
//            System.out.println("[User]: " + message);
         } catch (Exception e) {
            e.printStackTrace();
         }

      }

}
