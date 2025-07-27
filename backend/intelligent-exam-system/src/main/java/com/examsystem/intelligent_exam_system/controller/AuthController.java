package com.examsystem.intelligent_exam_system.controller;

import com.examsystem.intelligent_exam_system.dto.LoginRequest;
import com.examsystem.intelligent_exam_system.dto.LoginResponse;
import com.examsystem.intelligent_exam_system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // 允许跨域，方便前端测试
public class AuthController {

    @Autowired
    private AuthService authService;

    // 🔐 登录接口
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        // 验证输入
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(false, "用户名不能为空"));
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(false, "密码不能为空"));
        }

        LoginResponse response = authService.login(loginRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 🔑 修改初始密码接口
    @PostMapping("/change-initial-password")
    public ResponseEntity<LoginResponse> changeInitialPassword(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String newPassword = request.get("newPassword");

        // 验证输入
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(false, "用户名不能为空"));
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(false, "新密码不能为空"));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(false, "密码长度至少6位"));
        }

        LoginResponse response = authService.changeInitialPassword(username, newPassword);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 🧪 测试Token解析 (测试用)
    @GetMapping("/test-token")
    public ResponseEntity<?> testToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // 这里只是简单的测试，实际项目中会有更完善的Token验证
            return ResponseEntity.ok(Map.of(
                    "message", "Token接收成功",
                    "token", authHeader));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Token解析失败: " + e.getMessage()));
        }
    }
}