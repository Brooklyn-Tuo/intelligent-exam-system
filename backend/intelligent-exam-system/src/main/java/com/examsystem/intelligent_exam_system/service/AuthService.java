package com.examsystem.intelligent_exam_system.service;

import com.examsystem.intelligent_exam_system.dto.LoginRequest;
import com.examsystem.intelligent_exam_system.dto.LoginResponse;
import com.examsystem.intelligent_exam_system.entity.User;
import com.examsystem.intelligent_exam_system.repository.UserRepository;
import com.examsystem.intelligent_exam_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.examsystem.intelligent_exam_system.dto.ChangePasswordRequest;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // 1. 查找用户
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

            if (userOptional.isEmpty()) {
                return new LoginResponse(false, "用户名不存在");
            }

            User user = userOptional.get();

            // 2. 验证密码
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return new LoginResponse(false, "密码错误");
            }

//            // 3. 检查是否首次登录
//            if (user.getIsFirstLogin()) {
//                return new LoginResponse(false, "FIRST_LOGIN_REQUIRED");
//            }
            // 取消首次登录检查,在前端判断是否需要修改密码


            // 4. 生成JWT Token
            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getRole().name(),
                    user.getId());

            // 5. 构建用户信息
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().name(),
                    user.getFullName(),
                    user.getIsFirstLogin());

            // 6. 返回成功响应
            return new LoginResponse(true, "登录成功", token, userInfo);

        } catch (Exception e) {
            return new LoginResponse(false, "登录失败：" + e.getMessage());
        }
    }

    // 修改初始密码
    public LoginResponse changeInitialPassword(String username, String newPassword) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                return new LoginResponse(false, "用户不存在");
            }

            User user = userOptional.get();

            // 更新密码并取消首次登录标记
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setIsFirstLogin(false);
            userRepository.save(user);

            return new LoginResponse(true, "密码修改成功，请重新登录");

        } catch (Exception e) {
            return new LoginResponse(false, "密码修改失败：" + e.getMessage());
        }
    }



    // 修改密码（登陆后修改密码，用于用户登陆后随时修改密码）
    public LoginResponse changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return new LoginResponse(false, "用户不存在");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return new LoginResponse(false, "旧密码错误");
        }

        // 修改密码并保存
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new LoginResponse(true, "密码修改成功");
    }

}