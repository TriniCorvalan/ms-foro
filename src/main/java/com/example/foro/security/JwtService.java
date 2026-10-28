package com.example.foro.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	public String extractEmail(String token) {
		return parseClaims(token).getSubject();
	}

	public Long extractUserId(String token) {
		Object uid = parseClaims(token).get("uid");
		if (uid instanceof Integer i) {
			return i.longValue();
		}
		if (uid instanceof Long l) {
			return l;
		}
		return Long.valueOf(uid.toString());
	}

	public String extractRol(String token) {
		return String.valueOf(parseClaims(token).get("rol"));
	}

	public boolean isTokenValid(String token) {
		try {
			return !parseClaims(token).getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
