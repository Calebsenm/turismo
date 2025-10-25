package com.app.turismo.config.Jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.turismo.model.UsuarioEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

   @Value("${application.security.jwt.secret-key}")
   private String secretKey;

   @Value("${application.security.jwt.expiration}")
   private long jwtExpiration;

   public String generateToken(UsuarioEntity user) {
       Map<String, Object> claims = new HashMap<>();
       claims.put("id", user.getUser_id());
       claims.put("role", user.getUserType());


       return Jwts.builder()
           .setClaims(claims)
           .setSubject(user.getEmail())
           .setIssuedAt(new Date(System.currentTimeMillis()))
           .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
           .signWith(getSignKey(), SignatureAlgorithm.HS256)
           .compact();
   }

   public String extractUsername(String token) {
       return Jwts.parserBuilder()
           .setSigningKey(getSignKey())
           .build()
           .parseClaimsJws(token)
           .getBody()
           .getSubject();
   }
   private Key getSignKey() {
       byte[] keyBytes = Decoders.BASE64.decode(secretKey);
       return Keys.hmacShaKeyFor(keyBytes);
   }
}