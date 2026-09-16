package com.example.vertice.repository;

import com.example.vertice.model.PasswordResetToken;
import com.example.vertice.model.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

	private final Map<UUID, UserAccount> users = new ConcurrentHashMap<>();
	private final Map<String, PasswordResetToken> resetTokens = new ConcurrentHashMap<>();

	public UserAccount save(UserAccount user) {
		users.put(user.getId(), user);
		return user;
	}

	public Optional<UserAccount> findById(UUID id) {
		return Optional.ofNullable(users.get(id));
	}

	public Optional<UserAccount> findByUsername(String username) {
		return users.values().stream()
				.filter(user -> user.getUsername().equalsIgnoreCase(username))
				.findFirst();
	}

	public Optional<UserAccount> findByEmail(String email) {
		return users.values().stream()
				.filter(user -> user.getEmail().equalsIgnoreCase(email))
				.findFirst();
	}

	public Optional<UserAccount> findByUsernameOrEmail(String login) {
		Optional<UserAccount> byUsername = findByUsername(login);
		if (byUsername.isPresent()) {
			return byUsername;
		}
		return findByEmail(login);
	}

	public boolean existsByUsername(String username) {
		return findByUsername(username).isPresent();
	}

	public boolean existsByEmail(String email) {
		return findByEmail(email).isPresent();
	}

	public PasswordResetToken saveToken(PasswordResetToken token) {
		resetTokens.values().removeIf(existing -> existing.getUserId().equals(token.getUserId()));
		resetTokens.put(token.getToken(), token);
		return token;
	}

	public Optional<PasswordResetToken> findToken(String token) {
		return Optional.ofNullable(resetTokens.get(token));
	}

	public void deleteToken(String token) {
		resetTokens.remove(token);
	}
}
