package com.pagape.api.service;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

// Servicio encargado de generar y validar los tokens JWT
// JWT = JSON Web Token, es la "llave" que el servidor da al usuario tras el login
@Service
public class JwtService {

    // Spring lee estos valores directamente desde application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // Convierte nuestra clave secreta (String) en un objeto Key que entiende JJWT
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Genera un token JWT con el email del usuario dentro
    // Se llama justo después de verificar que el login es correcto
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // Email del usuario
                .setIssuedAt(new Date()) // Fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // Fecha de expiración
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firmamos el token
                .compact(); // Lo convertimos en String
    }

    // Extrae el email que está guardado dentro del token
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Comprueba si un token es válido y no ha expirado
    // Devuelve true si todo está bien, false si está manipulado o caducado
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
