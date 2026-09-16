package com.example.vertice.controller;

import com.example.vertice.model.UserAccount;
import com.example.vertice.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	private final AuthService authService;

	public PageController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/")
	public String root() {
		return "redirect:/home";
	}

	@GetMapping("/home")
	public String home(Authentication authentication, Model model) {
		populate(authentication, model);
		return "home";
	}

	@GetMapping("/admin")
	public String admin(Authentication authentication, Model model) {
		populate(authentication, model);
		return "admin";
	}

	private void populate(Authentication authentication, Model model) {
		String username = authentication.getName();
		UserAccount account = authService.findByUsername(username).orElse(null);
		model.addAttribute("username", username);
		model.addAttribute("displayName", account != null ? account.getName() : username);
		model.addAttribute("email", account != null ? account.getEmail() : "");
		model.addAttribute("isAdmin", authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
	}
}
