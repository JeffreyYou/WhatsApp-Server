package com.jeffrey.whatsapp.service;

import com.jeffrey.whatsapp.entity.GreenMessage;
import com.jeffrey.whatsapp.entity.WhatsappUser;
import com.jeffrey.whatsapp.mapper.WhatsappUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Slf4j
@Service
public class HandleRequest {

   @Autowired
   WhatsappUserMapper whatsappUserMapper;
      public void handleRequest(GreenMessage data){
         String phone = data.getBody().getSenderData().getSender();
         WhatsappUser whatsappUser = whatsappUserMapper.getUserByPhone(phone);

         if (whatsappUser == null) {
            insertUserIntoTable(data, phone);
         } else {
            StringBuilder builder = new StringBuilder("ws://");
            String url = "localhost:8080";
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
            log.info("ws uri: {}", uri);
         }

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

}
