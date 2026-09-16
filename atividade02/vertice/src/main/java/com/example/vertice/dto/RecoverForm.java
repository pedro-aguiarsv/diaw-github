package com.example.vertice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RecoverForm {

	@NotBlank(message = "Informe o e-mail.")
	@Email(message = "Informe um e-mail válido.")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
