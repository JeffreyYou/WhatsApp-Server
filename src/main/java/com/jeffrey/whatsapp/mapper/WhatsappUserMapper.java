package com.jeffrey.whatsapp.mapper;

import com.jeffrey.whatsapp.entity.WhatsappUser;
import org.apache.ibatis.annotations.Param;

public interface WhatsappUserMapper {

   public WhatsappUser getUserById(@Param("id") Long id);

   public WhatsappUser getUserByPhone(@Param("phone") String phone);

   public void addUser(@Param("user") WhatsappUser user);
}
