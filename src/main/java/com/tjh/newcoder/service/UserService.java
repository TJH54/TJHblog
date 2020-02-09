package com.tjh.newcoder.service;

import com.tjh.newcoder.dao.UserMapper;
import com.tjh.newcoder.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User find(int id){
        return userMapper.selectById(id);
    }
}
