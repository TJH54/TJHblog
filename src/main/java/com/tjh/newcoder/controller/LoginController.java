package com.tjh.newcoder.controller;

import com.tjh.newcoder.entity.User;
import com.tjh.newcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import static com.tjh.newcoder.util.CommunityConstant.ACTIVATION_FAILURE;
import static com.tjh.newcoder.util.CommunityConstant.ACTIVATION_REPEAT;
import static com.tjh.newcoder.util.CommunityConstant.ACTIVATION_SUCCESS;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    //跳转注册页面
    @RequestMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }
    //跳转登录页面
    @RequestMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }
    //注册提交页面
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(Model model,User user){

        Map<String,Object> map = userService.register(user);
        //根据结果判断 注册成功跳转到中转页面
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功！已向您邮箱中发送一封激活邮件，请注意查收");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            //校验有问题 跳转到注册页面并带校验信息
            System.out.println(map);
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }


    }

    //激活链接页面
    @RequestMapping("/activation/{userId}/{code}")
    public String activation(Model model,
                             @PathVariable("userId")int userId,
                             @PathVariable("code") String code){
        int res = userService.activate(userId,code);
        //数据库查用户 未激活 ==》 激活 转登录页
        if(res==ACTIVATION_SUCCESS){
            model.addAttribute("msg","您的账号已经激活成功,请登录");
            model.addAttribute("target","/site/login");
        }
        //数据库查用户 已激活 ==》 提示重复激活 转主页
        if(res==ACTIVATION_REPEAT){
            model.addAttribute("msg","您的账号重复激活");
            model.addAttribute("target","/index");
        }

        //数据库查用户 激活码错误 ==》 提示激活码错误 转主页
        if(res==ACTIVATION_FAILURE){
            model.addAttribute("msg","激活失败，请检查您的激活网站");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";

    }

}
