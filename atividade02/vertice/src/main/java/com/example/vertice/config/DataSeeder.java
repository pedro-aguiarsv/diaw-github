package com.example.vertice.config;

import com.example.vertice.model.Role;
import com.example.vertice.model.UserAccount;
import com.example.vertice.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(ApplicationArguments args) {
		userRepository.save(new UserAccount(
				"Administrador",
				"admin",
				"admin@vertice.local",
				passwordEncoder.encode("Admin1234"),
				Role.ADMIN
		));
		userRepository.save(new UserAccount(
				"Aluno Vértice",
				"aluno",
				"aluno@vertice.local",
				passwordEncoder.encode("Aluno1234"),
				Role.USER
		));
	}
}
