package com.tjh.newcoder.controller;


import com.tjh.newcoder.annotation.LoginRequired;
import com.tjh.newcoder.dao.UserMapper;
import com.tjh.newcoder.entity.User;
import com.tjh.newcoder.service.UserService;
import com.tjh.newcoder.util.CommunityUtil;
import com.tjh.newcoder.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${blog.path.upload}")
    private String uploadPath;

    @Value("${blog.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //设置页面
    @LoginRequired
    @RequestMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    //上传头像
    @LoginRequired
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "site/setting";
        }
        //MultipartFile表示HTML中以表单形式上传的文件（包含二进制数据和文件名称）
        //获取上传文件的后缀名
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式错误");
            return "/site/setting";
        }
        //生成随机文件名（防止重复）
        fileName = CommunityUtil.generateUUID() + suffix;
        //设置文件在服务器本地保存的路径
        File dest = new File(uploadPath + "/" + fileName);
        //将用户选择的文件上传到服务器指定路径下
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        //更新用户头像路径（一定是WEB访问路径）
        // http://localhost:8080/tjhblog/user/header/xxx.png
        User user = hostHolder.getUser();
        String url = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), url);

        return "redirect:/index";
    }


    //获取头像的WEB访问路径(相应图片逻辑返回值为空，使用response处理)
    @RequestMapping("/header/{filename}")
    public void getHeader(HttpServletResponse response, @PathVariable("filename") String filename) {
        //服务器存放路径
        filename = uploadPath + "/" + filename;
        //获取文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //相应指定格式图片
        response.setContentType("image/" + suffix);
        //文件流需要关闭 这样写自动关闭
        try (FileInputStream fis = new FileInputStream(filename);
             OutputStream os = response.getOutputStream();) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    //修改密码
    @RequestMapping(value = "/updatePwd",method = RequestMethod.POST)
    public String updatePwd(Model model, String oldPassword,String newPassword) {
        //查询用户密码（原密码是否正确、新密码是否和原密码相同）
        User user = hostHolder.getUser();
        String oldpwd = CommunityUtil.md5(oldPassword+user.getSalt());
        if(!oldpwd.equals(user.getPassword())){
            model.addAttribute("oldpwdMsg", "原密码错误！");
            return "/site/setting";
        }
        if (user.getPassword().equals(newPassword)) {
            model.addAttribute("newpwdMsg", "用户密码和原密码相同！");
            return "/site/setting";
        }
        //修改密码
        userService.updatePassword(user.getId(), newPassword);
        return "redirect:/index";

    }
}
