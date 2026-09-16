package com.example.vertice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterForm {

	@NotBlank(message = "Informe o nome.")
	@Size(min = 2, max = 80, message = "O nome deve ter entre 2 e 80 caracteres.")
	private String name;

	@NotBlank(message = "Informe o usuário.")
	@Size(min = 3, max = 32, message = "O usuário deve ter entre 3 e 32 caracteres.")
	@Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Use apenas letras, números, ponto, hífen ou sublinhado.")
	private String username;

	@NotBlank(message = "Informe o e-mail.")
	@Email(message = "Informe um e-mail válido.")
	private String email;

	@NotBlank(message = "Informe a senha.")
	@Size(min = 8, max = 72, message = "A senha deve ter pelo menos 8 caracteres.")
	private String password;

	@NotBlank(message = "Confirme a senha.")
	private String confirmPassword;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
