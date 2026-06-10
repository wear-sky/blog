package com.wearsky.demo.user.common;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RefreshScope
public class JwtUtil {

    @Value("${jwt.private-key-path}")
    private Resource privateKeyResource;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        log.debug("初始化JwtUtil");
        this.privateKey = loadPrivateKey();
    }

    public String generateToken(Long userId, List<String> authorities) {
        log.debug("令牌过期时间：{}ms", expirationMs);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .id(UUID.randomUUID().toString())
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(privateKey)
                .compact();
    }

    private PrivateKey loadPrivateKey() {
        try {
            String key = new String(privateKeyResource.getInputStream().readAllBytes());
            byte[] decoded = Base64.getDecoder().decode(key);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        } catch (Exception e) {
            log.error("加载私钥失败", e);
            System.exit(0);
            return null;
        }
    }
}