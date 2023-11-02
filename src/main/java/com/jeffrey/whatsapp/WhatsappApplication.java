package com.jeffrey.whatsapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.jeffrey.whatsapp.mapper")
@SpringBootApplication
public class WhatsappApplication {

   public static void main(String[] args) {
      SpringApplication.run(WhatsappApplication.class, args);
   }

}
