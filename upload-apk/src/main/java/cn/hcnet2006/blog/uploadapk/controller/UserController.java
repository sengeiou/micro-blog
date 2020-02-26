package cn.hcnet2006.blog.uploadapk.controller;

import cn.hcnet2006.blog.uploadapk.annotation.SysLogger;
import cn.hcnet2006.blog.uploadapk.security.UserDetailsServiceImpl;
import cn.hcnet2006.blog.uploadapk.vo.UserLoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @SysLogger("login")
    @PostMapping("/login")
    public UserLoginDTO login(@RequestParam("username") String username, @RequestParam("password")String password){
        return userDetailsService.login(username,password);
    }
    @RequestMapping("/foo")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String getFoo(){
        return "UPLOAD-APK-2";
    }
}
