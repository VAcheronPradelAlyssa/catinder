package com.catinder.dto;

import jakarta.validation.constraints.NotNull;

// DTO de swipe: transporte l'action utilisateur (like/dislike) et le résultat côté front.
public record SwipeDto(
	@NotNull
	Long catId,

	@NotNull
	Boolean liked,

	// Optionnel dans la requête, utile dans la réponse pour indiquer un match.
	Boolean match
) {
}
