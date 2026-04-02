package com.catinder.service;

import com.catinder.dto.SwipeDto;
import com.catinder.model.Cat;
import com.catinder.model.Swipe;
import com.catinder.model.User;
import com.catinder.repository.CatRepository;
import com.catinder.repository.SwipeRepository;
import com.catinder.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Service swipe: persiste likes/dislikes et retourne le résultat métier (match éventuel).
@Service
@Transactional
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final UserRepository userRepository;
    private final CatRepository catRepository;

    @Value("${catinder.match.probability:0.3}")
    private double matchProbability;

    public SwipeService(
        SwipeRepository swipeRepository,
        UserRepository userRepository,
        CatRepository catRepository
    ) {
        this.swipeRepository = swipeRepository;
        this.userRepository = userRepository;
        this.catRepository = catRepository;
    }

    // Enregistre (ou met à jour) un swipe user<->cat, puis calcule le match pour un like.
    public SwipeDto swipe(Long userId, SwipeDto request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        Cat cat = catRepository.findById(request.catId())
            .orElseThrow(() -> new EntityNotFoundException("Chat introuvable."));

        Optional<Swipe> existing = swipeRepository.findByUserIdAndCatId(userId, request.catId());
        Swipe swipe = existing.orElseGet(() -> Swipe.builder().user(user).cat(cat).build());

        swipe.setLiked(Boolean.TRUE.equals(request.liked()));
        swipe.setSwipedAt(LocalDateTime.now());
        swipeRepository.save(swipe);

        boolean match = isMatch(Boolean.TRUE.equals(request.liked()));
        return new SwipeDto(cat.getId(), Boolean.TRUE.equals(request.liked()), match);
    }

    private boolean isMatch(boolean liked) {
        if (!liked) {
            return false;
        }
        return ThreadLocalRandom.current().nextDouble() < matchProbability;
    }
}
