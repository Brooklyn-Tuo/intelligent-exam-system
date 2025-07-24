package com.examsystem.intelligent_exam_system.repository;

import com.examsystem.intelligent_exam_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// 用户数据访问层接口，继承JpaRepository，提供常用的查询方法
// Spring会自动识别继承JpaRepositoy的接口，并自动生成实现类，并注入到Spring容器中，所以注解可以省略。
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 泛型参数说明，
    // User 管理的实体类
    // Long 实体主键类型

    /***
     * 根据用户名查找用户，
     * findBy+属性名(首字母大写)->生成查询：where username = ？
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * 生成查询：Whereemail = ？
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户是否存在
     * 存在return true 不存在return false
     * 生成查询：select count(*) > 0 from users where username = ?
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在，
     * 存在返回true 不存在返回false
     * 生成查询 select count（*） > 0 from users where email = ?
     */
    boolean existsByEmail(String email);
    // ▶ 更多查询方法示例（根据需要添加）：
    // List<User> findByRole(Role role); // 按角色查询
    // List<User> findByDepartmentOrderByFullNameAsc(String dept);// 按部门查询+姓名排序
    // Page<User> findByClassName(String className, Pageable pg); // 分页查询

    // ▶ 复杂查询可使用 @Query 注解：
    // @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.className LIKE
    // %?1%")
    // List<User> findStudentsByClassPattern(String pattern);
}
