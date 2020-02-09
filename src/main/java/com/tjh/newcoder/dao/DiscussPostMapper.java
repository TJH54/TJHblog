package com.tjh.newcoder.dao;

import com.tjh.newcoder.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,
                                         @Param("current")int current,
                                         @Param("limit") int limit);

    int selectDiscussPostRows(@Param("userId") int userId);
}
