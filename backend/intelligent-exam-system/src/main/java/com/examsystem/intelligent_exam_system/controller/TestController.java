package com.examsystem.intelligent_exam_system.controller;

import com.examsystem.intelligent_exam_system.entity.Role;
import com.examsystem.intelligent_exam_system.entity.User;
import com.examsystem.intelligent_exam_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserService userService;

    // 🧪 测试接口：创建测试用户
    @PostMapping("/create-users")
    public ResponseEntity<?> createTestUsers() {
        try {
            // 创建管理员
            User admin = userService.createUser(
                    "admin",
                    "admin@school.edu",
                    "admin123",
                    Role.ADMIN,
                    "系统管理员");

            // 创建教师
            User teacher = userService.createUser(
                    "teacher01",
                    "teacher01@school.edu",
                    "teacher123",
                    Role.TEACHER,
                    "张老师");

            // 创建学生
            User student = userService.createUser(
                    "student01",
                    "student01@school.edu",
                    "student123",
                    Role.STUDENT,
                    "李同学");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "测试用户创建成功！");
            response.put("users", List.of(
                    Map.of("username", admin.getUsername(), "role", admin.getRole()),
                    Map.of("username", teacher.getUsername(), "role", teacher.getRole()),
                    Map.of("username", student.getUsername(), "role", student.getRole())));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "创建用户失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 🧪 测试接口：查看所有用户
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", users.size());
        response.put("users", users);

        return ResponseEntity.ok(response);
    }

    // 🧪 测试接口：验证密码加密
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户不存在"));
        }

        boolean isValid = userService.verifyPassword(password, user.getPassword());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "username", username,
                "passwordValid", isValid,
                "storedPassword", user.getPassword() // 🔍 可以看到加密后的密码
        ));
    }
}