package com.examsystem.intelligent_exam_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类，对应数据库中的 users 表
 * 使用 JPA 注解定义实体与表的映射关系
 */
@Entity // 标记该类为JPA实体，声明这是持久化实体类
@Table(name = "users") // 指定映射的表名，默认类名小写,指定对应的数据库表名
public class User {

    // ------------------- 主键字段 -------------------
    @Id // 标记为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略：数据库自增
    private Long id;

    // ------------------- 用户认证信息 -------------------
    @Column(unique = true, nullable = false, length = 50) // 唯一约束，非空，长度限制50
    private String username; // 用户名（登录账号）

    @Column(unique = true, nullable = false, length = 100) // 唯一约束，非空
    private String email; // 电子邮箱

    @Column(nullable = false) // 非空
    private String password; // 密码（存储时应为加密后的哈希值）

    // ------------------- 用户角色 -------------------
    @Enumerated(EnumType.STRING) // 存储枚举的字符串值（非序号）
    @Column(nullable = false)
    private Role role; // 用户角色：STUDENT/TEACHER/ADMIN

    // ------------------- 用户基本信息 -------------------
    @Column(name = "full_name", nullable = false, length = 100) // 数据库列名映射
    private String fullName; // 用户全名

    @Column(name = "student_id", length = 20)
    private String studentId; // 学号（学生专用）

    @Column(name = "employee_id", length = 20)
    private String employeeId; // 工号（教职工专用）

    // ------------------- 附加信息 -------------------
    @Column(name = "class_name", length = 50)
    private String className; // 班级名称（学生专用）

    @Column(length = 50)
    private String department; // 院系/部门

    // ------------------- 系统字段 -------------------
    @Column(name = "is_first_login")
    private Boolean isFirstLogin = true; // 是否首次登录（默认true）

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 记录创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 最后更新时间

    // ------------------- 构造方法 -------------------
    /**
     * JPA规范要求无参构造函数
     */
    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public Boolean getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(Boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
