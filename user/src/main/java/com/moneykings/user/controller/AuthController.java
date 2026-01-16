package com.moneykings.gateway.controller;

import com.moneykings.gateway.config.SecurityConfig;
import com.moneykings.gateway.dto.request.LoginRequest;
import com.moneykings.gateway.dto.response.LoginResponse;
import com.moneykings.gateway.entity.User;
import com.moneykings.gateway.service.UserService;
import com.moneykings.gateway.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private SecurityConfig securityConfig;

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        log.info("登录请求: 邮箱={}", request.getEmail());
        try {
            // 1. 查询用户（修复空指针问题）
            User user = userService.findByUserName(request.getEmail());
            if (user == null) {
                log.warn("用户不存在: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("用户名不存在");
            }

            log.info("找到用户: ID={}, 邮箱={}", user.getLoginId(), user.getEmail());

            // 2. 验证密码（使用 BCrypt 比对）
            String rawPassword = request.getPassword();
            String encodedPassword = user.getPassword();

            if (rawPassword == null || encodedPassword == null) {
                log.warn("密码为空: raw={}, encoded={}",
                        rawPassword != null ? "有值" : "null",
                        encodedPassword != null ? "有值" : "null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("用户名或密码错误");
            }

            boolean passwordMatches = securityConfig.passwordEncoder()
                    .matches(rawPassword, encodedPassword);

            if (!passwordMatches) {
                log.warn("密码不匹配: 用户={}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("用户名或密码错误");
            }

            // 3. 生成 JWT
            String token = jwtUtil.generateToken(user.getEmail(), user.getLoginId());
            log.info("生成Token成功: 用户={}, Token长度={}",
                    user.getEmail(), token.length());

            // 4. 返回成功响应
            LoginResponse response = new LoginResponse(
                    user.getLoginId(),
                    user.getEmail(),
                    token
            );

            log.info("登录成功: 用户={}", user.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("登录异常: 邮箱={}, 错误={}",
                    request.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("登录失败: " + e.getMessage());
        }
    }

}
