package com.jeffrey.whatsapp.controller;

import com.jeffrey.whatsapp.entity.WhatsappUser;
import com.jeffrey.whatsapp.mapper.WhatsappUserMapper;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/green")
@RestController
public class GreenAPI {

   @Autowired
   WhatsappUserMapper whatsappUserMapper;

   @Autowired
   StringRedisTemplate stringRedisTemplate;

      @GetMapping("/test")
      public String test() {
         return "green test";
      }

      @GetMapping("/user/{id}")
      public WhatsappUser getUserById(@PathVariable("id") Long id) {
         WhatsappUser user = whatsappUserMapper.getUserById(id);

         return user;
      }


      @GetMapping("count")
      public String count() {
         Long hello = stringRedisTemplate.opsForValue().increment("hello");
         return "Visited [" + hello + "] times";
      }
}
