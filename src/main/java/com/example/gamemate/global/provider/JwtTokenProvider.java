package com.example.gamemate.global.provider;

import com.example.gamemate.domain.user.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;
    //Todo 변수명 단위 보이도록 바꿀지 고민
    private final long accessTokenExpirationTime = 1000 * 60 * 60; //60분

    public String createAccessToken(String email, Role role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role.getName());

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationTime);
        Key signingKey = generateSigningKey();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signingKey)
                .compact();
    }


    public boolean validateToken(String token) {
        try{
            getTokenClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.", e);
        }
    }

    public String getEmailFromToken(String token) {
        return getTokenClaims(token).getSubject();
    }

    private Key generateSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }


    private Claims getTokenClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



}
