package ai.smartdoc.garage.auth.internal.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
class JwtService {

    private final Key key;
    private final long accessExpiryMinutes;
    private final long refreshExpiryDays;

    JwtService(@Value("${app.jwt.access-expiry-minutes}") long accessExpiryMinutes,
               @Value("${app.jwt.refresh-expiry-days}") long refreshExpiryDays,
               @Value("${app.jwt.secret}") String secret) {
        if (secret == null || secret.length() < 32)
            throw new IllegalArgumentException("JWT secret must be >= 32 chars");

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiryMinutes = accessExpiryMinutes;
        this.refreshExpiryDays = refreshExpiryDays;
    }

    public String createAccessToken(String id) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessExpiryMinutes * 60)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String id, String sessionId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(id)
                .claim("sid", sessionId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshExpiryDays * 86400)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public long refreshExpiryMillis() {
        return refreshExpiryDays * 24L * 3600L * 1000L;
    }
}
