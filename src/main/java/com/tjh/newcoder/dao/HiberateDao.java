package com.tjh.newcoder.dao;

import org.springframework.stereotype.Repository;

@Repository("hibernate")
public class HiberateDao implements AlphaDao {
    @Override
    public String find() {
        return "this is hibernate";
    }
}
