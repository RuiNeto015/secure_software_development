package com.desofs.backend.services;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
	
	private final JwtDecoder jwtDecoder;

	private final Set<String> validTokens = Collections.synchronizedSet(new HashSet<String>());

	public void addToken(String token) {
		validTokens.add(token);
	}

	public void removeToken(String token) {
		validTokens.remove(token);
	}

	@Scheduled(cron = "* * 3 * * *")
	public void clearTokens() {
		validTokens.forEach(token -> {
			Jwt jwtToken = jwtDecoder.decode(token);
			if (!isTokenDateValid(jwtToken)) {
				validTokens.remove(token);
			}
		});
	}

	public boolean validToken(String token) {
		return validTokens.contains(token);
	}

	private boolean isTokenDateValid(Jwt token) {
		return token != null && token.getExpiresAt() != null && token.getExpiresAt().isAfter(new Date().toInstant());
	}
}
