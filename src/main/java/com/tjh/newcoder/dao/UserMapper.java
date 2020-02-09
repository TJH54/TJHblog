package com.tjh.newcoder.dao;

import com.tjh.newcoder.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


public interface UserMapper {

    User selectById(int id);

    User selectByName(String name);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(@Param("id") int id, @Param("status")int status);

    int updateHeader(@Param("id")int id,@Param("headerUrl")String headerUrl);

    int updatePassword(@Param("id")int id,@Param("password")String password);

}
