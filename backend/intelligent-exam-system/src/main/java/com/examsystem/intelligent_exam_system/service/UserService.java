package com.examsystem.intelligent_exam_system.service;

import com.examsystem.intelligent_exam_system.entity.User;
import com.examsystem.intelligent_exam_system.entity.Role;
import com.examsystem.intelligent_exam_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 创建用户（密码会自动加密）
    public User createUser(String username, String email, String rawPassword,
            Role role, String fullName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword)); // 🔑 关键：加密密码
        user.setRole(role);
        user.setFullName(fullName);
        user.setIsFirstLogin(true);

        return userRepository.save(user);
    }

    // 验证密码
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 查找用户
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}