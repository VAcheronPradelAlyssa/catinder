package com.catinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de lecture d'un profil chat pour l'écran de swipe.
public record CatDto(
	@NotNull
	Long id,

	@NotBlank
	@Size(min = 2, max = 32)
	String name,

	@Size(max = 280)
	String bio,

	@Size(max = 500)
	String photoUrl
) {
}
