package com.catinder.repository;

import com.catinder.model.Cat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Repository Cat : lecture des profils et sélection du prochain chat à proposer.
public interface CatRepository extends JpaRepository<Cat, Long> {

	@Query("""
		select c
		from Cat c
		where c.id not in (
			select s.cat.id
			from Swipe s
			where s.user.id = :userId
		)
		""")
	Page<Cat> findNotSwipedByUserId(@Param("userId") Long userId, Pageable pageable);
}
