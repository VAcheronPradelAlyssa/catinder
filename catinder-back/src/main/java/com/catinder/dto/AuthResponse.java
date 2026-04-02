package com.catinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO de sortie d'authentification: retourne uniquement les infos nécessaires au client.
public record AuthResponse(
	@NotBlank
	String token,

	@NotBlank
	String tokenType,

	@NotNull
	Long userId,

	@NotBlank
	String login,

	@NotBlank
	String email
) {
}
