package com.tjh.newcoder.service;

import com.tjh.newcoder.dao.LoginTicketMapper;
import com.tjh.newcoder.dao.UserMapper;
import com.tjh.newcoder.entity.LoginTicket;
import com.tjh.newcoder.entity.User;
import com.tjh.newcoder.util.CommunityUtil;
import com.tjh.newcoder.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.tjh.newcoder.util.CommunityConstant.*;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${server.servlet.context-path}")
    private String path;

    public User find(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //判断合法性--判空
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("EmailMsg", "邮箱不能为空");
            return map;
        }
        //判断合法性--账号信息
        // u用于返回数据库查找结果，这个方法传入参数user在方法结束后还会在controller中用到，不要混淆
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "用户已存在");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("EmailMsg", "该邮箱已被注册");
            return map;
        }
        //数据库插入用户信息
        String salt = CommunityUtil.generateUUID().substring(0, 5);
        user.setSalt(salt);
        String pwd = CommunityUtil.md5(user.getPassword() + salt);
        user.setPassword(pwd);
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        String url = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(100));
        user.setHeaderUrl(url);
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String activationUrl = "localhost:8080" + path + "/activation/"
                + user.getId() + "/" + user.getActivationCode();

        context.setVariable("url", activationUrl);
        String content = templateEngine.process("/mail/activation", context);
        System.out.println(content);
        mailClient.sendMail(user.getEmail(), "激活邮件", content);
        return map;
    }

    public int activate(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getActivationCode().equals(code) && user.getStatus() == 0) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else if (user.getActivationCode().equals(code) && user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //判空
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空");
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
        }
        //判断用户是否存在
        User u = userMapper.selectByName(username);
        if (u == null) {
            map.put("usernameMsg", "用户不存在");
        }
        //判断账户密码是否匹配
        if (!u.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误");
        }
        //判断账户是否激活
        if (u.getStatus() == 0) {
            map.put("usernameMsg", "用户未激活");
        }
        //登录通过后，生成登录凭证ticket
        // 设置expired
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(u.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertTicket(loginTicket);
        //返回凭证用于controller发送cookie
        map.put("cookie", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        loginTicketMapper.updateStatus(loginTicket.getUserId(), 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        return loginTicket;
    }

    public User findById(int id) {
        User user = userMapper.selectById(id);
        return user;
    }
}
