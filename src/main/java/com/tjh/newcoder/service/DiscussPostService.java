package com.tjh.newcoder.service;

import com.tjh.newcoder.dao.DiscussPostMapper;
import com.tjh.newcoder.dao.UserMapper;
import com.tjh.newcoder.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId,int current,int limit){
        return discussPostMapper.selectDiscussPosts(userId,current,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }



}
