package com.tjh.newcoder;

import com.tjh.newcoder.dao.DiscussPostMapper;
import com.tjh.newcoder.dao.LoginTicketMapper;
import com.tjh.newcoder.dao.UserMapper;
import com.tjh.newcoder.entity.DiscussPost;
import com.tjh.newcoder.entity.LoginTicket;
import com.tjh.newcoder.entity.User;
import com.tjh.newcoder.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NewcoderApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginMapper;

    @Test
    public void testUserMapper() {
        User user = userMapper.selectById(25);
        System.out.println(user);
        user = userMapper.selectByName("nowcoder23");
        System.out.println(user);
        user = userMapper.selectByEmail("nowcoder24@sina.com");
        System.out.println(user);
        User user1 = new User("lyn", "0108", "123",
                "222", 0, 0, "123", "123", new Date());
        //userMapper.insertUser(user1);

    }

    @Test
    public void testUpdate() {
        userMapper.updateStatus(149, 0);
        userMapper.updateHeader(149, "http://www.baidu.com");
        userMapper.updatePassword(149, "liuyuning");
    }

    @Test
    public void testService() {
        User user = userService.find(149);
        System.out.println(user);
    }

    @Test
    public void testPosts() {
        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(0, 1, 20);
        for (DiscussPost post : posts) {
            System.out.println(post);
        }
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }

    @Test
    public void testInserLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("tjh");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 60 * 10));
        loginMapper.insertTicket(loginTicket);
    }

    @Test
    public void testLoginTicket(){
        LoginTicket loginTicket = loginMapper.selectByTicket("tjh");
        System.out.println(loginTicket);
        loginMapper.updateStatus(101,1);
    }
}
