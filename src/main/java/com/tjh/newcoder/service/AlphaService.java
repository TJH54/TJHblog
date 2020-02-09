package com.tjh.newcoder.service;

import com.tjh.newcoder.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {
    @Autowired
    @Qualifier("hibernate")
    private AlphaDao alphaDao;
    public String getData(){
        return alphaDao.find();
    }

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }


}
