package com.examsystem.intelligent_exam_system.repository;

import com.examsystem.intelligent_exam_system.entity.User;
import com.examsystem.intelligent_exam_system.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层接口，继承JpaRepository，提供常用的查询方法
 * Spring会自动识别继承JpaRepository的接口，并自动生成实现类，并注入到Spring容器中
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 泛型参数说明:
    // User: 管理的实体类
    // Long: 实体主键类型

    // ==================== 基础查询方法 ====================

    /**
     * 根据用户名查找用户
     * findBy+属性名(首字母大写) -> 生成查询: WHERE username = ?
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * 生成查询: WHERE email = ?
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     * 存在返回true，不存在返回false
     * 生成查询: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * 存在返回true，不存在返回false
     * 生成查询: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);

    // ==================== 角色相关查询 ====================

    /**
     * 根据角色查询用户 - 不分页
     * 生成查询: WHERE role = ?
     */
    List<User> findByRole(Role role);

    /**
     * 根据角色查询用户 - 支持分页
     * 生成查询: WHERE role = ? LIMIT ? OFFSET ?
     */
    Page<User> findByRole(Role role, Pageable pageable);

    /**
     * 统计指定角色的用户数量
     * 生成查询: SELECT COUNT(*) FROM users WHERE role = ?
     */
    long countByRole(Role role);

    // ==================== 状态相关查询 ====================

    /**
     * 根据首次登录状态查询用户
     * 生成查询: WHERE is_first_login = ?
     */
    List<User> findByIsFirstLogin(Boolean isFirstLogin);

    /**
     * 统计首次登录用户数量
     * 生成查询: SELECT COUNT(*) FROM users WHERE is_first_login = ?
     */
    long countByIsFirstLogin(Boolean isFirstLogin);

    // ==================== 组织结构相关查询 ====================

    /**
     * 根据部门查询用户
     * 生成查询: WHERE department = ?
     */
    List<User> findByDepartment(String department);

    /**
     * 根据部门查询用户 - 支持分页
     */
    Page<User> findByDepartment(String department, Pageable pageable);

    /**
     * 根据角色和班级查询用户（主要用于查询某班学生）
     * 生成查询: WHERE role = ? AND class_name = ?
     */
    List<User> findByRoleAndClassName(Role role, String className);

    /**
     * 根据班级查询学生 - 支持分页
     */
    Page<User> findByRoleAndClassName(Role role, String className, Pageable pageable);

    // ==================== 搜索功能 ====================

    /**
     * 根据用户名或全名模糊搜索
     * 生成查询: WHERE username LIKE %?1% OR full_name LIKE %?2%
     */
    List<User> findByUsernameContainingOrFullNameContaining(String username, String fullName);

    /**
     * 根据用户名模糊搜索 - 支持分页
     */
    Page<User> findByUsernameContaining(String username, Pageable pageable);

    /**
     * 根据全名模糊搜索 - 支持分页
     */
    Page<User> findByFullNameContaining(String fullName, Pageable pageable);

    // ==================== 复杂查询 (使用@Query注解) ====================

    /**
     * 查询某个部门的特定角色用户
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.department = :department")
    List<User> findByRoleAndDepartment(@Param("role") Role role, @Param("department") String department);

    /**
     * 查询最近创建的用户
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    Page<User> findRecentUsers(Pageable pageable);

    /**
     * 根据关键词搜索用户（搜索用户名、全名、邮箱）
     */
    @Query("SELECT u FROM User u WHERE " +
            "u.username LIKE %:keyword% OR " +
            "u.fullName LIKE %:keyword% OR " +
            "u.email LIKE %:keyword%")
    List<User> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 搜索用户 - 支持分页
     */
    @Query("SELECT u FROM User u WHERE " +
            "u.username LIKE %:keyword% OR " +
            "u.fullName LIKE %:keyword% OR " +
            "u.email LIKE %:keyword%")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 统计各部门的用户数量
     */
    @Query("SELECT u.department, COUNT(u) FROM User u GROUP BY u.department")
    List<Object[]> countUsersByDepartment();

    /**
     * 查询指定时间范围内创建的用户
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findUsersByDateRange(@Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 查询需要重置密码的用户（首次登录且超过指定天数未登录）
     */
    @Query("SELECT u FROM User u WHERE u.isFirstLogin = true AND u.createdAt < :cutoffDate")
    List<User> findUsersNeedPasswordReset(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);

    // ==================== 批量操作相关 ====================

    /**
     * 根据ID列表查询用户
     */
    List<User> findByIdIn(List<Long> ids);

    /**
     * 根据用户名列表查询用户
     */
    List<User> findByUsernameIn(List<String> usernames);

    /**
     * 删除指定角色的所有用户（慎用！）
     */
    void deleteByRole(Role role);

    // ==================== 统计查询 ====================

    /**
     * 统计各角色用户数量
     */
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countUsersByRole();

    /**
     * 统计各班级学生数量
     */
    @Query("SELECT u.className, COUNT(u) FROM User u WHERE u.role = 'STUDENT' GROUP BY u.className")
    List<Object[]> countStudentsByClass();

    /**
     * 查询活跃用户（非首次登录）
     */
    @Query("SELECT u FROM User u WHERE u.isFirstLogin = false")
    List<User> findActiveUsers();

    /**
     * 统计活跃用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isFirstLogin = false")
    long countActiveUsers();

    // ==================== 排序查询示例 ====================

    /**
     * 按创建时间降序查询用户
     */
    List<User> findAllByOrderByCreatedAtDesc();

    /**
     * 按全名升序查询用户
     */
    List<User> findAllByOrderByFullNameAsc();

    /**
     * 按角色、创建时间排序查询
     */
    List<User> findAllByOrderByRoleAscCreatedAtDesc();

    // ==================== 自定义原生SQL查询示例 ====================

    /**
     * 使用原生SQL查询用户统计信息
     */
    @Query(value = "SELECT " +
            "COUNT(*) as total_users, " +
            "SUM(CASE WHEN role = 'STUDENT' THEN 1 ELSE 0 END) as student_count, " +
            "SUM(CASE WHEN role = 'TEACHER' THEN 1 ELSE 0 END) as teacher_count, " +
            "SUM(CASE WHEN role = 'ADMIN' THEN 1 ELSE 0 END) as admin_count, " +
            "SUM(CASE WHEN is_first_login = true THEN 1 ELSE 0 END) as first_login_count " +
            "FROM users", nativeQuery = true)
    Object[] getUserStatistics();

    // ==================== 更多示例方法（根据需要添加） ====================

    /**
     * 查找某个部门的教师
     */
    List<User> findByRoleAndDepartmentOrderByFullNameAsc(Role role, String department);

    /**
     * 查找学号不为空的学生
     */
    List<User> findByRoleAndStudentIdIsNotNull(Role role);

    /**
     * 查找工号不为空的教职工
     */
    List<User> findByRoleInAndEmployeeIdIsNotNull(List<Role> roles);

    /**
     * 查找指定班级的学生数量
     */
    long countByRoleAndClassName(Role role, String className);

    /**
     * 查找邮箱域名匹配的用户
     */
    List<User> findByEmailEndingWith(String domain);
}