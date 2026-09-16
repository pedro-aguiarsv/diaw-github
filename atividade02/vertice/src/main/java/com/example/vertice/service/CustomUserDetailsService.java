package com.example.vertice.service;

import com.example.vertice.model.UserAccount;
import com.example.vertice.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		UserAccount account = userRepository.findByUsernameOrEmail(login)
				.orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));

		return User.builder()
				.username(account.getUsername())
				.password(account.getPasswordHash())
				.roles(account.getRole().name())
				.build();
	}
}
