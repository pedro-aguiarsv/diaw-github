package com.example.vertice.controller;

import com.example.vertice.dto.RecoverForm;
import com.example.vertice.dto.RegisterForm;
import com.example.vertice.dto.ResetPasswordForm;
import com.example.vertice.service.AuthService;
import com.example.vertice.service.RecoverResult;
import com.example.vertice.service.RegistrationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register(Model model) {
		if (!model.containsAttribute("form")) {
			model.addAttribute("form", new RegisterForm());
		}
		return "register";
	}

	@PostMapping("/register")
	public String handleRegister(
			@Valid @ModelAttribute("form") RegisterForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "register";
		}
		try {
			authService.register(form);
		} catch (RegistrationException exception) {
			bindingResult.reject("register", exception.getMessage());
			return "register";
		}
		redirectAttributes.addFlashAttribute("successMessage", "Conta criada. Entre com seu usuário ou e-mail.");
		return "redirect:/login?registered";
	}

	@GetMapping("/recoverpassword")
	public String recoverPassword(Model model) {
		if (!model.containsAttribute("form")) {
			model.addAttribute("form", new RecoverForm());
		}
		return "recoverpassword";
	}

	@PostMapping("/recoverpassword")
	public String handleRecoverPassword(
			@Valid @ModelAttribute("form") RecoverForm form,
			BindingResult bindingResult,
			HttpServletRequest request,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "recoverpassword";
		}
		RecoverResult result = authService.requestPasswordReset(form.getEmail(), baseUrl(request));
		model.addAttribute("sent", true);
		model.addAttribute("demoMode", result.demoMode());
		model.addAttribute("resetLink", result.resetLink());
		return "recoverpassword";
	}

	@GetMapping("/reset-password")
	public String resetPassword(@RequestParam(value = "token", required = false) String token, Model model) {
		ResetPasswordForm form = new ResetPasswordForm();
		form.setToken(token);
		model.addAttribute("form", form);
		model.addAttribute("invalidToken", token == null || token.isBlank());
		return "reset-password";
	}

	@PostMapping("/reset-password")
	public String handleResetPassword(
			@Valid @ModelAttribute("form") ResetPasswordForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("invalidToken", false);
			return "reset-password";
		}
		try {
			authService.resetPassword(form.getToken(), form.getPassword(), form.getConfirmPassword());
		} catch (RegistrationException exception) {
			bindingResult.reject("reset", exception.getMessage());
			model.addAttribute("invalidToken", false);
			return "reset-password";
		}
		redirectAttributes.addFlashAttribute("successMessage", "Senha atualizada. Entre com a nova senha.");
		return "redirect:/login?reset";
	}

	private String baseUrl(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-Host");
		if (forwarded != null && !forwarded.isBlank()) {
			String proto = request.getHeader("X-Forwarded-Proto");
			return (proto == null ? "http" : proto) + "://" + forwarded;
		}
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
	}
}
