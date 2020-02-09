package com.tjh.newcoder.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("mybatis")
@Primary
public class MybatisDao implements AlphaDao {
    @Override
    public String find() {
        return "this is mybatis";
    }
}
