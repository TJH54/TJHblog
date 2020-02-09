package com.tjh.newcoder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.tjh.newcoder.dao")
@SpringBootApplication
public class NewcoderApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewcoderApplication.class, args);
    }

}
