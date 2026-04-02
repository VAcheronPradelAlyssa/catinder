package com.catinder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO d'entrée pour les endpoints d'authentification (register/login selon le flux choisi).
public record AuthRequest(
	@NotBlank(message = "Le login est requis")
	@Size(min = 3, max = 32, message = "Le login doit contenir entre 3 et 32 caractères")
	String login,

	@NotBlank(message = "L'email est requis")
	@Email(message = "Le format de l'email est invalide")
	@Size(max = 120, message = "L'email est trop long")
	String email,

	@NotBlank(message = "Le mot de passe est requis")
	@Size(min = 8, max = 100, message = "Le mot de passe doit contenir entre 8 et 100 caractères")
	String password
) {
}
