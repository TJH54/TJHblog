package com.tjh.newcoder.controller.intercepter;

import com.tjh.newcoder.dao.LoginTicketMapper;
import com.tjh.newcoder.dao.UserMapper;
import com.tjh.newcoder.entity.LoginTicket;
import com.tjh.newcoder.entity.User;
import com.tjh.newcoder.service.UserService;
import com.tjh.newcoder.util.CookieUtil;
import com.tjh.newcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
@Component
public class LoginTicketIntercepter implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //判断浏览器中是否有cookie
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            //根据凭证查找LoginTicket
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                User user = userService.findById(loginTicket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        hostHolder.clear();
    }
}
