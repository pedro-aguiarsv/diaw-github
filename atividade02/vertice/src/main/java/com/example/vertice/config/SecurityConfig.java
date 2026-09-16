package com.example.vertice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/css/**",
								"/js/**",
								"/images/**",
								"/error"
						).permitAll()
						.requestMatchers(HttpMethod.GET, "/login", "/register", "/recoverpassword", "/reset-password").permitAll()
						.requestMatchers(HttpMethod.POST, "/register", "/recoverpassword", "/reset-password").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage("/login")
						.usernameParameter("username")
						.passwordParameter("password")
						.failureUrl("/login?error")
						.defaultSuccessUrl("/home", true)
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
						.permitAll()
				)
				.exceptionHandling(ex -> ex
						.accessDeniedPage("/error")
				);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
