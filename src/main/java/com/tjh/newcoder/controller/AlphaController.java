package com.tjh.newcoder.controller;

import com.sun.deploy.net.HttpResponse;
import com.tjh.newcoder.service.AlphaService;
import com.tjh.newcoder.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    @Value("${server.servlet.context-path}")
    private String path;

    //测试controller-service-dao
    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.getData();
    }

    //GET请求---参数拼在路径后面 ？....
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "students";
    }

    //GET请求---参数作为路径的一部分
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String student(@PathVariable("id") int id) {
        System.out.println(id);
        return "student";
    }

    //POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String addStudent(String name, int age) {
        System.out.println("姓名" + name);
        System.out.println("年龄" + age);
        return "add success";
    }

    //响应Html数据---ModelAndView形式
    @RequestMapping("/teacher")
    public ModelAndView teacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "lyn");
        mav.addObject("age", 30);
        mav.setViewName("/demo/view");
        return mav;
    }

    //响应Html数据---model数据渲染view形式
    @RequestMapping("/school")
    public String school(Model model) {
        model.addAttribute("name", "北京邮电大学");
        model.addAttribute("age", 90);
        return "/demo/view";
    }

    //响应JSON数据---常用于异步请求
    @RequestMapping("/emp")
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "lyn");
        emp.put("age", 30);
        emp.put("salary", 3000);
        return emp;
    }

    @RequestMapping("/emps")
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> emps = new ArrayList<>();

        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("name", "tjh");
        emp1.put("age", 20);
        emp1.put("salary", 7000);
        emps.add(emp1);

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("name", "lyn");
        emp2.put("age", 30);
        emp2.put("salary", 3000);
        emps.add(emp2);
        return emps;
    }

    //cookie示例
    @RequestMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("cookie", "lyn");
        cookie.setPath(path + "/alpha");
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("cookie") String cookie) {
        System.out.println(cookie);
        return "get cookie";
    }

    //session示例
    @RequestMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("session", CommunityUtil.generateUUID());
        return "set session";
    }

    @RequestMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session) {
        session.getAttribute("session");
        return "get session";
    }


}
