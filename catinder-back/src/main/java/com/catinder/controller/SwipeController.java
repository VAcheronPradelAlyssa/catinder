package com.catinder.controller;

import com.catinder.dto.SwipeDto;
import com.catinder.model.User;
import com.catinder.repository.UserRepository;
import com.catinder.service.SwipeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Contrôleur swipe : enregistre l'action utilisateur (like/dislike) sur un chat.
@RestController
@RequestMapping("/swipes")
public class SwipeController {

	private final SwipeService swipeService;
	private final UserRepository userRepository;

	public SwipeController(SwipeService swipeService, UserRepository userRepository) {
		this.swipeService = swipeService;
		this.userRepository = userRepository;
	}

	@PostMapping
	public ResponseEntity<SwipeDto> createSwipe(@Valid @RequestBody SwipeDto request, Principal principal) {
		User user = userRepository.findByLogin(principal.getName())
			.orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

		SwipeDto response = swipeService.swipe(user.getId(), request);
		return ResponseEntity.ok(response);
	}
}
