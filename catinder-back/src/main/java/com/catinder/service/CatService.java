package com.catinder.service;

import com.catinder.dto.CatDto;
import com.catinder.model.Cat;
import com.catinder.repository.CatRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

// Service chat: sélectionne le prochain profil à swiper pour un utilisateur.
@Service
@Transactional
public class CatService {

    private final CatRepository catRepository;

    public CatService(CatRepository catRepository) {
        this.catRepository = catRepository;
    }

    // Retourne un chat non encore swipé par l'utilisateur. Vide si plus de chat disponible.
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<CatDto> getNextCatForUser(Long userId) {
        // On lit un petit lot pour pouvoir varier le résultat sans full scan.
        Page<Cat> candidates = catRepository.findNotSwipedByUserId(userId, PageRequest.of(0, 20));
        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        List<Cat> cats = candidates.getContent();
        Cat selected = cats.get(ThreadLocalRandom.current().nextInt(cats.size()));
        return Optional.of(toDto(selected));
    }

    // Méthode utilitaire de mapping entité -> DTO exposé au front.
    public CatDto toDto(Cat cat) {
        return new CatDto(cat.getId(), cat.getName(), cat.getBio(), cat.getImageUrl());
    }
}
