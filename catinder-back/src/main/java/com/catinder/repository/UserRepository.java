package com.catinder.repository;

import com.catinder.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository User : accès aux utilisateurs pour auth, inscription et vérification d'unicité.
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByLogin(String login);

	boolean existsByEmail(String email);

	boolean existsByLogin(String login);
}
