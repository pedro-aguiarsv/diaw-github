package com.example.vertice.model;

import java.time.Instant;
import java.util.UUID;

public class PasswordResetToken {

	private final String token;
	private final UUID userId;
	private final Instant expiresAt;

	public PasswordResetToken(UUID userId, Instant expiresAt) {
		this.token = UUID.randomUUID().toString();
		this.userId = userId;
		this.expiresAt = expiresAt;
	}

	public String getToken() {
		return token;
	}

	public UUID getUserId() {
		return userId;
	}

	public boolean isExpired() {
		return Instant.now().isAfter(expiresAt);
	}
}
