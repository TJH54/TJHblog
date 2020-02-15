package com.tjh.newcoder.controller;

import com.google.code.kaptcha.Producer;
import com.tjh.newcoder.entity.LoginTicket;
import com.tjh.newcoder.entity.User;
import com.tjh.newcoder.service.UserService;
import com.tjh.newcoder.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    public final static Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;


    @Value("${server.servlet.context-path}")
    private String contextPath;

    //跳转注册页面
    @RequestMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    //跳转登录页面
    @RequestMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    //注册提交页面
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {

        Map<String, Object> map = userService.register(user);
        //根据结果判断 注册成功跳转到中转页面
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功！已向您邮箱中发送一封激活邮件，请注意查收");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            //校验有问题 跳转到注册页面并带校验信息
            System.out.println(map);
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }


    }

    //激活链接页面
    @RequestMapping("/activation/{userId}/{code}")
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        int res = userService.activate(userId, code);
        //数据库查用户 未激活 ==》 激活 转登录页
        if (res == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "您的账号已经激活成功,请登录");
            model.addAttribute("target", "/site/login");
        }
        //数据库查用户 已激活 ==》 提示重复激活 转主页
        if (res == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "您的账号重复激活");
            model.addAttribute("target", "/index");
        }

        //数据库查用户 激活码错误 ==》 提示激活码错误 转主页
        if (res == ACTIVATION_FAILURE) {
            model.addAttribute("msg", "激活失败，请检查您的激活网站");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";

    }

    //生成验证码
    @RequestMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //验证码存入session
        session.setAttribute("kaptcha", text);
        //图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("验证码生成失败：" + e.getMessage());
        }
    }
    //登录逻辑
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(Model model,HttpSession session,String username,String password,String code,
                        boolean rememberme,HttpServletResponse response){
        //校验验证码
        String kaptcha = (String)session.getAttribute("kaptcha");
        if(StringUtils.isBlank(code)||StringUtils.isBlank(kaptcha)|| !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "/site/login";
        }

        int expired = rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        //校验账户
        Map<String,Object> map = userService.login(username,password,expired);
        if(map.get("cookie")!=null){
            Cookie cookie = new Cookie("ticket",map.get("cookie").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expired);
            response.addCookie(cookie);
            return "redirect:/index";
        }
        model.addAttribute("usernameMsg",map.get("usernameMsg"));
        model.addAttribute("passwordMsg",map.get("passwordMsg"));
        return "/site/login";
    }
    @RequestMapping("/logout")
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }

}
