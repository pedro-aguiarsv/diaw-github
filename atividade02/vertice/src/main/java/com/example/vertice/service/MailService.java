package com.example.vertice.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MailService {

	private final JavaMailSender mailSender;
	private final boolean enabled;
	private final String from;

	public MailService(
			ObjectProvider<JavaMailSender> mailSender,
			@Value("${app.mail.enabled:false}") boolean enabled,
			@Value("${app.mail.from:}") String from) {
		this.mailSender = mailSender.getIfAvailable();
		this.from = from;
		this.enabled = enabled && this.mailSender != null && StringUtils.hasText(from);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void send(String to, String subject, String body) {
		if (!enabled) {
			return;
		}
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);
	}
}
