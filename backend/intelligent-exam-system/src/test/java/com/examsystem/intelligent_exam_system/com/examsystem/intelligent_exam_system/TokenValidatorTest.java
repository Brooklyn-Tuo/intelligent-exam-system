package com.examsystem.intelligent_exam_system.com.examsystem.intelligent_exam_system;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;

public class TokenValidatorTest {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVEVBQ0hFUiIsInVzZXJJZCI6Miwic3ViIjoidGVhY2hlcjAxIiwiaWF0IjoxNzU0MzA4Njk2LCJleHAiOjE3NTQzOTUwOTZ9.Y2xlPbw95nOeitSIj9DbdR4yfC5GEK8FpqWW0o5ydPw";
        String secret = "mySecretKeyForExamSystemThatIsLongEnoughForHS256Algorithm";

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("✅ 验证通过，用户名: " + claims.getSubject());
        } catch (Exception e) {
            System.err.println("❌ 验证失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}