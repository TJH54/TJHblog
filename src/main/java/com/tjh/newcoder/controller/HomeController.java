package com.tjh.newcoder.controller;

import com.tjh.newcoder.entity.DiscussPost;
import com.tjh.newcoder.entity.Page;
import com.tjh.newcoder.entity.User;
import com.tjh.newcoder.service.DiscussPostService;
import com.tjh.newcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping("/index")
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> discussPostList =
                discussPostService.findDiscussPosts(0, page.getOffset(),page.getLimit());

        List<Map<String,Object>> list = new ArrayList<>();
        if(discussPostList!=null){
            for(DiscussPost post:discussPostList){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.find(post.getUserId());
                map.put("user",user);
                list.add(map);
            }
        }
        model.addAttribute("discussPosts",list);
        return "/index";
    }

}
