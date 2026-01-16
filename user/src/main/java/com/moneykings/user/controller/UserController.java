package com.moneykings.user.controller;

import com.moneykings.user.config.SecurityConfig;
import com.moneykings.user.dao.User;
import com.moneykings.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private SecurityConfig securityConfig;
    @PostMapping("/register")
    public ResponseEntity<?> User(@RequestBody User user){
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        boolean save = userService.save(user);
        if (save){
            return ResponseEntity.ok("注册成功");
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("注册失败");
        }

    }

}