package com.jeffrey.whatsapp.utils;

import com.jeffrey.whatsapp.entity.GreenMessage;
import com.jeffrey.whatsapp.entity.WhatsappUser;
import com.jeffrey.whatsapp.mapper.WhatsappUserMapper;
import com.jeffrey.whatsapp.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketUtils {

   @Autowired
   ConcurrentHashMap<String, WebSocketSession> userSessions;
   @Autowired
   WebSocketClient webSocketClient;
   @Autowired
   StringRedisTemplate redisTemplate;
   @Autowired
   WhatsappUserMapper whatsappUserMapper;
   @Autowired
   GreenApiUtils greenApiUtils;

   public WebSocketSession createSessionIfNotExist(WhatsappUser whatsappUser) throws Exception {
      String uri = generateUri(whatsappUser);
      String userId = String.valueOf(whatsappUser.getId());
      String phone = whatsappUser.getPhone();
      // create session if not exist
      if (!userSessions.containsKey(userId)) {
         connect(userId, uri, phone);
      }
      // check if the session is still alive
      if (!userSessions.get(userId).isOpen()) {
         connect(userId, uri, phone);
      }
      // session initialization

      return userSessions.get(userId);
   }

   public WhatsappUser createUserIfNotExist(GreenMessage data) {
      String phone = data.getBody().getSenderData().getSender();
      WhatsappUser whatsappUser = whatsappUserMapper.getUserByPhone(phone);

      if (whatsappUser == null) {
         insertUserIntoTable(data, phone);
         whatsappUser = whatsappUserMapper.getUserByPhone(phone);
      }
      return whatsappUser;
   }

//   public WebSocketSession initSession(WhatsappUser whatsappUser) throws IOException {
//
//      return userSessions.get(userId);
//   }

   private class MessageHandler extends TextWebSocketHandler {
      private StringBuilder sb = new StringBuilder();
      @Override
      public void handleTextMessage(WebSocketSession session, TextMessage message) {
         String messagePayload = message.getPayload();
         if (messagePayload.startsWith("Select your character by entering the corresponding number:") || messagePayload.startsWith("[end]")) {
            return;
         }
         String phone = getPhone(session);
         System.out.print(message.getPayload());
         if (!messagePayload.startsWith("[end=")) {
            sb.append(messagePayload);
         } else {
            greenApiUtils.sendMessageToUser(sb.toString(), phone);
            sb.setLength(0);
         }
      }

      private String getPhone(WebSocketSession session) {
         String userId = session.getHandshakeHeaders().get("user_id").get(0);
         Object o = redisTemplate.opsForHash().get("aliveSession", userId);
         if (o == null) {
            o = whatsappUserMapper.getPhoneByUserId(Long.valueOf(userId));
         }
         return (String) o;
      }
   }

   // Helper functions
   private void connect(String userId, String uri, String phone) throws Exception {
      WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
      headers.add("user_id", userId);
      WebSocketSession session = webSocketClient.doHandshake(
                  new MessageHandler(), headers,
                  URI.create(uri))
                  .get();

      userSessions.put(userId, session);
      session.sendMessage(new TextMessage("Terminal"));
      session.sendMessage(new TextMessage("1"));
      redisTemplate.opsForHash().put("aliveSession", userId, phone);

   }


   private void insertUserIntoTable(GreenMessage data, String phone) {
      StringBuilder hexString = new StringBuilder();
      try {
         // generate phone hash
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(phone.getBytes("UTF-8"));

         for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      String phoneHash = hexString.toString().substring(0, 40);
      WhatsappUser whatsappUser = WhatsappUser.builder().phone(phone).phoneHash(phoneHash).build();
      whatsappUserMapper.addUser(whatsappUser);
   }
   private String generateUri(WhatsappUser whatsappUser) {
      StringBuilder builder = new StringBuilder("ws://");
      String url = "192.168.1.73:8080";
      String api_key = "";
      String llm_model = "gpt-3.5-turbo-16k";
      builder.append(url)
            .append("/ws/")
            .append(whatsappUser.getPhoneHash())
            .append("?api_key=")
            .append(api_key)
            .append("&llm_model=")
            .append(llm_model);
      String uri = builder.toString();
      return uri;
   }
}
