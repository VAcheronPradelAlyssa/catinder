package com.catinder.repository;

import com.catinder.model.Swipe;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository Swipe : persistance des actions like/dislike et consultation de l'historique utilisateur.
public interface SwipeRepository extends JpaRepository<Swipe, Long> {

	List<Swipe> findByUserIdOrderBySwipedAtDesc(Long userId);

	Optional<Swipe> findByUserIdAndCatId(Long userId, Long catId);

	boolean existsByUserIdAndCatId(Long userId, Long catId);

	long countByUserIdAndLikedTrue(Long userId);
}
