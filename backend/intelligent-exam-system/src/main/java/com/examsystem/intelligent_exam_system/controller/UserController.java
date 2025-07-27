package com.examsystem.intelligent_exam_system.controller;

import com.examsystem.intelligent_exam_system.dto.UserDto;
import com.examsystem.intelligent_exam_system.dto.CreateUserRequest;
import com.examsystem.intelligent_exam_system.dto.UpdateUserRequest;
import com.examsystem.intelligent_exam_system.entity.User;
import com.examsystem.intelligent_exam_system.entity.Role;
import com.examsystem.intelligent_exam_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户管理控制器 - RESTful API 实现
 * 提供标准的CRUD操作接口
 * 
 * API设计遵循REST原则：
 * - GET /api/users - 获取用户列表
 * - POST /api/users - 创建新用户
 * - GET /api/users/{id} - 获取特定用户
 * - PUT /api/users/{id} - 更新用户信息
 * - DELETE /api/users/{id} - 删除用户
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // 允许跨域访问
public class UserController {

    @Autowired
    private UserService userService;

    // ==================== 查询操作 ====================

    /**
     * 获取用户列表 - 支持分页和角色筛选
     * GET /api/users?page=0&size=10&role=STUDENT
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role) {

        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page, size);

            // 根据角色筛选
            List<User> users;
            if (role != null && !role.isEmpty()) {
                try {
                    Role roleEnum = Role.valueOf(role.toUpperCase());
                    users = userService.getUsersByRole(roleEnum, pageable);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(
                            new ApiResponse(false, "无效的角色: " + role));
                }
            } else {
                users = userService.getAllUsers(pageable);
            }

            // 转换为DTO
            List<UserDto> userDtos = users.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse(true, "用户列表获取成功", userDtos));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "获取用户列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取特定用户
     * GET /api/users/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOptional = userService.getUserById(id);

            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserDto userDto = convertToDto(userOptional.get());
            return ResponseEntity.ok(new ApiResponse(true, "用户信息获取成功", userDto));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "获取用户信息失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户名查找用户
     * GET /api/users/search?username=john_doe
     */
    @GetMapping("/search")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        try {
            Optional<User> userOptional = userService.findByUsername(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserDto userDto = convertToDto(userOptional.get());
            return ResponseEntity.ok(new ApiResponse(true, "用户查找成功", userDto));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "用户查找失败: " + e.getMessage()));
        }
    }

    // ==================== 创建操作 ====================

    /**
     * 创建新用户
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            // 验证输入
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "用户名不能为空"));
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "邮箱不能为空"));
            }

            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "密码长度至少6位"));
            }

            // 检查用户名是否已存在
            if (userService.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "用户名已存在"));
            }

            // 检查邮箱是否已存在
            if (userService.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "邮箱已存在"));
            }

            // 创建用户
            User newUser = userService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole() != null ? request.getRole() : Role.STUDENT,
                    request.getFullName());

            UserDto userDto = convertToDto(newUser);
            return ResponseEntity.ok(new ApiResponse(true, "用户创建成功", userDto));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "用户创建失败: " + e.getMessage()));
        }
    }

    // ==================== 更新操作 ====================

    /**
     * 更新用户信息
     * PUT /api/users/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            Optional<User> userOptional = userService.getUserById(id);

            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User updatedUser = userService.updateUser(id, request);
            UserDto userDto = convertToDto(updatedUser);

            return ResponseEntity.ok(new ApiResponse(true, "用户信息更新成功", userDto));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "用户信息更新失败: " + e.getMessage()));
        }
    }

    // ==================== 删除操作 ====================

    /**
     * 删除用户
     * DELETE /api/users/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> userOptional = userService.getUserById(id);

            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse(true, "用户删除成功"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "用户删除失败: " + e.getMessage()));
        }
    }

    // ==================== 统计接口 ====================

    /**
     * 获取用户统计信息
     * GET /api/users/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getUserStatistics() {
        try {
            UserStatistics stats = userService.getUserStatistics();
            return ResponseEntity.ok(new ApiResponse(true, "用户统计信息获取成功", stats));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "获取用户统计信息失败: " + e.getMessage()));
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 实体转DTO
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setFullName(user.getFullName());
        dto.setStudentId(user.getStudentId());
        dto.setEmployeeId(user.getEmployeeId());
        dto.setClassName(user.getClassName());
        dto.setDepartment(user.getDepartment());
        dto.setIsFirstLogin(user.getIsFirstLogin());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    // ==================== 响应类 ====================

    /**
     * 统一API响应格式
     */
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    /**
     * 用户统计信息类
     */
    public static class UserStatistics {
        private long totalUsers;
        private long studentCount;
        private long teacherCount;
        private long adminCount;

        public UserStatistics(long totalUsers, long studentCount, long teacherCount, long adminCount) {
            this.totalUsers = totalUsers;
            this.studentCount = studentCount;
            this.teacherCount = teacherCount;
            this.adminCount = adminCount;
        }

        // Getters and Setters
        public long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public long getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(long studentCount) {
            this.studentCount = studentCount;
        }

        public long getTeacherCount() {
            return teacherCount;
        }

        public void setTeacherCount(long teacherCount) {
            this.teacherCount = teacherCount;
        }

        public long getAdminCount() {
            return adminCount;
        }

        public void setAdminCount(long adminCount) {
            this.adminCount = adminCount;
        }
    }
}