package com.jeffrey.whatsapp.service;

import com.jeffrey.whatsapp.entity.WhatsappUser;
import com.jeffrey.whatsapp.utils.WebSocketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketService {

   @Autowired
   WebSocketUtils webSocketUtils;

   public WebSocketSession establishSession(WhatsappUser whatsappUser) throws Exception {

      return webSocketUtils.createSessionIfNotExist(whatsappUser);
   }


}
