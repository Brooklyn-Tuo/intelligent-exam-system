package com.examsystem.intelligent_exam_system.controller;

import com.examsystem.intelligent_exam_system.dto.LoginRequest;
import com.examsystem.intelligent_exam_system.dto.LoginResponse;
import com.examsystem.intelligent_exam_system.service.AuthService;
import com.examsystem.intelligent_exam_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.examsystem.intelligent_exam_system.dto.ChangePasswordRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // 允许跨域，方便前端测试
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtUtil jwtUtil;

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

    // 🔑 修改初始密码接口（用于用户第一次登陆后强制修改密码）
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



    // 🔐 修改密码接口（登陆后修改密码，用于用户登陆后随时修改密码）
    // ✅ 登录后修改密码接口
    // ✅ 登录用户修改密码接口（通过JWT验证身份）
    @PostMapping("/change-password")
    public ResponseEntity<LoginResponse> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest request) {

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "缺少或无效的Token"));
        }

        // 提取Token
        String token = authHeader.substring(7);




        // 👇👇 新增打印，放在这里 👇👇
        System.out.println(">>> Authorization header: " + authHeader);
        System.out.println(">>> Token after substring: " + token);




        // 提取用户名
        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "无效的Token"));
        }

        // 输入验证
        if (oldPassword == null || newPassword == null ||
                oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new LoginResponse(false, "密码不能为空"));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(new LoginResponse(false, "新密码长度至少6位"));
        }

        LoginResponse response = authService.changePassword(username, oldPassword, newPassword);

        return response.isSuccess() ?
                ResponseEntity.ok(response) :
                ResponseEntity.badRequest().body(response);
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