package com.tjh.newcoder.dao;

import com.tjh.newcoder.entity.LoginTicket;
import org.apache.ibatis.annotations.Param;

public interface LoginTicketMapper {
    int insertTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(@Param("userId") int userId,@Param("status") int status);
}
