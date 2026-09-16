package com.example.vertice.model;

import java.time.Instant;
import java.util.UUID;

public class UserAccount {

	private final UUID id;
	private String name;
	private String username;
	private String email;
	private String passwordHash;
	private Role role;
	private final Instant createdAt;

	public UserAccount(String name, String username, String email, String passwordHash, Role role) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
		this.createdAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
