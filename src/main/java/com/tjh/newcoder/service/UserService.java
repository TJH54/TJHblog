package com.tjh.newcoder.service;

import com.tjh.newcoder.dao.UserMapper;
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

import static com.tjh.newcoder.util.CommunityConstant.ACTIVATION_FAILURE;
import static com.tjh.newcoder.util.CommunityConstant.ACTIVATION_REPEAT;
import static com.tjh.newcoder.util.CommunityConstant.ACTIVATION_SUCCESS;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
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

    public int activate(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getActivationCode().equals(code)&&user.getStatus()==0){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else if(user.getActivationCode().equals(code)&&user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

}
