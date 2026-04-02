package com.catinder.controller;

import com.catinder.dto.CatDto;
import com.catinder.model.User;
import com.catinder.repository.UserRepository;
import com.catinder.service.CatService;
import jakarta.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Contrôleur chat : expose les endpoints protégés de lecture de profils à swiper.
@RestController
@RequestMapping("/cats")
public class CatController {

	private final CatService catService;
	private final UserRepository userRepository;

	public CatController(CatService catService, UserRepository userRepository) {
		this.catService = catService;
		this.userRepository = userRepository;
	}

	@GetMapping("/next")
	public ResponseEntity<CatDto> getNextCat(Principal principal) {
		User user = userRepository.findByLogin(principal.getName())
			.orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

		Optional<CatDto> nextCat = catService.getNextCatForUser(user.getId());
		return nextCat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
	}
}
