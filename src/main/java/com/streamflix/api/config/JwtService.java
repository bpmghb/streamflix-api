package com.streamflix.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Gerar token JWT
     */
    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    /**
     * Gerar token JWT com claims extras
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return buildToken(extraClaims, username, jwtExpiration);
    }

    /**
     * Construir token JWT
     */
    private String buildToken(Map<String, Object> extraClaims, String username, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validar se token é válido
     */
    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username)) && !isTokenExpired(token);
    }

    /**
     * Extrair username do token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrair data de expiração do token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrair claim específico do token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrair todos os claims do token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verificar se token está expirado - TORNADO PÚBLICO
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Obter chave de assinatura
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gerar token com informações do usuário (username e perfil)
     */
    public String generateTokenWithUserInfo(String username, String perfil) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("perfil", perfil);
        return generateToken(claims, username);
    }

    /**
     * Extrair perfil do usuário do token
     */
    public String extractUserProfile(String token) {
        return extractClaim(token, claims -> claims.get("perfil", String.class));
    }

    /**
     * Validar token e extrair informações
     */
    public boolean validateTokenAndExtractInfo(String token, String username) {
        try {
            return isTokenValid(token, username);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verificar se token está prestes a expirar (próximos 30 minutos)
     */
    public boolean isTokenExpiringSoon(String token) {
        Date expiration = extractExpiration(token);
        long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
        return timeUntilExpiration < (30 * 60 * 1000); // 30 minutos em milissegundos
    }

    /**
     * Renovar token se estiver próximo do vencimento
     */
    public String renewTokenIfNeeded(String token) {
        if (isTokenExpiringSoon(token) && !isTokenExpired(token)) {
            String username = extractUsername(token);
            String perfil = extractUserProfile(token);
            return generateTokenWithUserInfo(username, perfil);
        }
        return token;
    }
}