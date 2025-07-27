package com.examsystem.intelligent_exam_system.service;

import com.examsystem.intelligent_exam_system.dto.UpdateUserRequest;
import com.examsystem.intelligent_exam_system.entity.User;
import com.examsystem.intelligent_exam_system.entity.Role;
import com.examsystem.intelligent_exam_system.repository.UserRepository;
import com.examsystem.intelligent_exam_system.controller.UserController.UserStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // 🔐 注入密码加密器

    // ==================== 查询操作 ====================

    /**
     * 获取所有用户 - 支持分页
     */
    public List<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    /**
     * 获取所有用户 - 不分页
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 根据ID查找用户
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名查找用户
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据邮箱查找用户
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 根据角色查找用户 - 支持分页
     */
    public List<User> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable).getContent();
    }

    /**
     * 根据角色查找用户 - 不分页
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ==================== 创建操作 ====================

    /**
     * 创建用户（密码会自动加密）- 基础版本
     */
    public User createUser(String username, String email, String rawPassword,
            Role role, String fullName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword)); // 🔑 关键：加密密码
        user.setRole(role);
        user.setFullName(fullName);
        user.setIsFirstLogin(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * 创建用户 - 完整信息版本
     */
    public User createUser(String username, String email, String rawPassword,
            Role role, String fullName, String studentId, String employeeId,
            String className, String department) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setFullName(fullName);
        user.setStudentId(studentId);
        user.setEmployeeId(employeeId);
        user.setClassName(className);
        user.setDepartment(department);
        user.setIsFirstLogin(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // ==================== 更新操作 ====================

    /**
     * 更新用户信息
     */
    public User updateUser(Long id, UpdateUserRequest request) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在，ID: " + id);
        }

        User user = userOptional.get();

        // 更新字段（只更新非空值）
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // 检查邮箱是否已被其他用户使用
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        if (request.getStudentId() != null) {
            user.setStudentId(request.getStudentId());
        }

        if (request.getEmployeeId() != null) {
            user.setEmployeeId(request.getEmployeeId());
        }

        if (request.getClassName() != null) {
            user.setClassName(request.getClassName());
        }

        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * 更新用户密码
     */
    public User updateUserPassword(Long id, String newPassword) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在，ID: " + id);
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * 重置首次登录状态
     */
    public User resetFirstLoginStatus(Long id, boolean isFirstLogin) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在，ID: " + id);
        }

        User user = userOptional.get();
        user.setIsFirstLogin(isFirstLogin);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // ==================== 删除操作 ====================

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在，ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * 批量删除用户
     */
    public void deleteUsers(List<Long> ids) {
        for (Long id : ids) {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
            }
        }
    }

    /**
     * 软删除用户 (如果将来需要)
     * 注意：需要在User实体中添加isDeleted字段
     */
    // public User softDeleteUser(Long id) {
    // Optional<User> userOptional = userRepository.findById(id);
    // if (userOptional.isEmpty()) {
    // throw new RuntimeException("用户不存在，ID: " + id);
    // }
    // User user = userOptional.get();
    // user.setIsDeleted(true);
    // user.setUpdatedAt(LocalDateTime.now());
    // return userRepository.save(user);
    // }

    // ==================== 密码相关操作 ====================

    /**
     * 验证密码
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 验证用户密码
     */
    public boolean verifyUserPassword(Long id, String rawPassword) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, userOptional.get().getPassword());
    }

    // ==================== 统计操作 ====================

    /**
     * 获取用户统计信息
     */
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long studentCount = userRepository.countByRole(Role.STUDENT);
        long teacherCount = userRepository.countByRole(Role.TEACHER);
        long adminCount = userRepository.countByRole(Role.ADMIN);

        return new UserStatistics(totalUsers, studentCount, teacherCount, adminCount);
    }

    /**
     * 按角色统计用户数量
     */
    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }

    /**
     * 统计首次登录用户数量
     */
    public long countFirstLoginUsers() {
        return userRepository.countByIsFirstLogin(true);
    }

    // ==================== 业务逻辑方法 ====================

    /**
     * 重置所有用户的首次登录状态（管理员功能）
     */
    public void resetAllUsersFirstLoginStatus() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setIsFirstLogin(true);
            user.setUpdatedAt(LocalDateTime.now());
        }
        userRepository.saveAll(users);
    }

    /**
     * 根据部门获取用户列表
     */
    public List<User> getUsersByDepartment(String department) {
        return userRepository.findByDepartment(department);
    }

    /**
     * 根据班级获取学生列表
     */
    public List<User> getStudentsByClassName(String className) {
        return userRepository.findByRoleAndClassName(Role.STUDENT, className);
    }

    /**
     * 搜索用户（按用户名或全名模糊搜索）
     */
    public List<User> searchUsers(String keyword) {
        return userRepository.findByUsernameContainingOrFullNameContaining(keyword, keyword);
    }

    /**
     * 批量创建用户（用于Excel导入等场景）
     */
    public List<User> createUsersInBatch(List<User> users) {
        // 加密所有用户的密码
        for (User user : users) {
            if (user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            user.setIsFirstLogin(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
        }
        return userRepository.saveAll(users);
    }

    // ==================== 验证方法 ====================

    /**
     * 验证用户创建数据
     */
    public void validateUserCreation(String username, String email) {
        if (existsByUsername(username)) {
            throw new RuntimeException("用户名已存在: " + username);
        }
        if (existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在: " + email);
        }
    }

    /**
     * 验证密码强度
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // 可以添加更复杂的密码强度验证逻辑
        // 例如：包含大小写字母、数字、特殊字符等
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        return hasLetter && hasDigit;
    }
}