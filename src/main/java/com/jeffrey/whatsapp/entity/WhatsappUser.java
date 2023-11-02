package com.jeffrey.whatsapp.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
public class WhatsappUser {
   private long id;
   private String phone;
   private String phoneHash;
   private String status;
   private Timestamp loginTime;
}
