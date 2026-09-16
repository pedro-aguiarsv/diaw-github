package com.example.vertice.service;

import com.example.vertice.dto.RegisterForm;
import com.example.vertice.model.PasswordResetToken;
import com.example.vertice.model.Role;
import com.example.vertice.model.UserAccount;
import com.example.vertice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AuthService {

	private static final Pattern PASSWORD_RULE = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).+$");

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
	}

	public void register(RegisterForm form) {
		String name = form.getName().trim();
		String username = form.getUsername().trim();
		String email = form.getEmail().trim().toLowerCase();

		if (!form.getPassword().equals(form.getConfirmPassword())) {
			throw new RegistrationException("As senhas não coincidem.");
		}
		validatePassword(form.getPassword());

		if (userRepository.existsByUsername(username)) {
			throw new RegistrationException("Este usuário já está em uso.");
		}
		if (userRepository.existsByEmail(email)) {
			throw new RegistrationException("Este e-mail já está cadastrado.");
		}

		UserAccount account = new UserAccount(
				name,
				username,
				email,
				passwordEncoder.encode(form.getPassword()),
				Role.USER
		);
		userRepository.save(account);
	}

	public RecoverResult requestPasswordReset(String rawEmail, String baseUrl) {
		String email = rawEmail == null ? "" : rawEmail.trim().toLowerCase();
		Optional<UserAccount> account = userRepository.findByEmail(email);

		if (account.isEmpty()) {
			return new RecoverResult(false, null);
		}

		PasswordResetToken token = new PasswordResetToken(account.get().getId(), Instant.now().plus(Duration.ofMinutes(30)));
		userRepository.saveToken(token);
		String resetLink = baseUrl + "/reset-password?token=" + token.getToken();

		if (mailService.isEnabled()) {
			String body = """
					Olá, %s.

					Recebemos um pedido para redefinir a senha da sua conta no Vértice.
					Abra o link abaixo em até 30 minutos:

					%s

					Se você não fez este pedido, ignore este e-mail.
					""".formatted(account.get().getName(), resetLink);
			mailService.send(account.get().getEmail(), "Vértice — redefinição de senha", body);
			return new RecoverResult(false, null);
		}

		return new RecoverResult(true, resetLink);
	}

	public void resetPassword(String tokenValue, String password, String confirmPassword) {
		if (!password.equals(confirmPassword)) {
			throw new RegistrationException("As senhas não coincidem.");
		}
		validatePassword(password);

		PasswordResetToken token = userRepository.findToken(tokenValue)
				.filter(item -> !item.isExpired())
				.orElseThrow(() -> new RegistrationException("Este link de recuperação é inválido ou já expirou."));

		UserAccount account = userRepository.findById(token.getUserId())
				.orElseThrow(() -> new RegistrationException("Não foi possível localizar a conta."));

		account.setPasswordHash(passwordEncoder.encode(password));
		userRepository.save(account);
		userRepository.deleteToken(tokenValue);
	}

	public Optional<UserAccount> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	private void validatePassword(String password) {
		if (!StringUtils.hasText(password) || password.length() < 8) {
			throw new RegistrationException("A senha deve ter pelo menos 8 caracteres.");
		}
		if (!PASSWORD_RULE.matcher(password).matches()) {
			throw new RegistrationException("A senha deve conter letras e números.");
		}
	}
}
