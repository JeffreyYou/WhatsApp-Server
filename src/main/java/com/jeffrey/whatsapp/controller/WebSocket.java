package com.jeffrey.whatsapp.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocket {

   @MessageMapping("/send")
   @SendTo("/topic/messages")
   public String handleMessage(String message) {
      return "Received: " + message;
   }
}
