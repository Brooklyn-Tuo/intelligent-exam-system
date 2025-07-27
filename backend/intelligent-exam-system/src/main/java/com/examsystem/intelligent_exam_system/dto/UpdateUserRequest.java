package com.examsystem.intelligent_exam_system.dto;

import com.examsystem.intelligent_exam_system.entity.Role;

/**
 * 更新用户请求DTO
 * 注意：通常不允许通过API更新用户名和密码
 */
public class UpdateUserRequest {
    private String email;
    private String fullName;
    private String studentId;
    private String employeeId;
    private String className;
    private String department;
    private Role role; // 通常只有管理员能修改角色

    // 构造函数
    public UpdateUserRequest() {
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}