package com.nick;

import com.nick.common.utils.JwtUtils;
import com.nick.sys.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtUtilsTest {
    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void testCreateJwt() {
        User user = new User();
        user.setUsername("zhangsan");
        user.setPassword("123456");
        String token = jwtUtils.createToken(user);
        System.out.println(token);
    }

    @Test
    public void testParseJwt() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJmMTk3ZDNhYi1lNTkwLTRiM2QtYTg1Yy01YmMxYTQ0YjU4ZDMiLCJzdWIiOiJ7XCJwYXNzd29yZFwiOlwiMTIzNDU2XCIsXCJ1c2VybmFtZVwiOlwiemhhbmdzYW5cIn0iLCJpc3MiOiJzeXN0ZW0iLCJpYXQiOjE3MzIzNDk3MDMsImV4cCI6MTczMjM1MTUwM30.zjvUIsL79DvUjQUG-RLpyDgLRvrd-tk_MqpiFFxL83s";
        Claims claims = jwtUtils.parseToken(token);
        System.out.println(claims);
    }

    @Test
    public void testParseJwt2() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJmMTk3ZDNhYi1lNTkwLTRiM2QtYTg1Yy01YmMxYTQ0YjU4ZDMiLCJzdWIiOiJ7XCJwYXNzd29yZFwiOlwiMTIzNDU2XCIsXCJ1c2VybmFtZVwiOlwiemhhbmdzYW5cIn0iLCJpc3MiOiJzeXN0ZW0iLCJpYXQiOjE3MzIzNDk3MDMsImV4cCI6MTczMjM1MTUwM30.zjvUIsL79DvUjQUG-RLpyDgLRvrd-tk_MqpiFFxL83s";
        User user = jwtUtils.parseToken(token, User.class);
        System.out.println(user);
    }
}
