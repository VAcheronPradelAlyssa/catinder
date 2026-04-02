package com.catinder.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

// Entité Swipe : relie un User à un Cat avec une action (like/dislike)
@Entity
@Table(name = "swipe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Swipe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cat_id", nullable = false)
	private Cat cat;

	@NotNull
	@Column(nullable = false)
	private Boolean liked; // true = like, false = dislike

	@Column(nullable = false)
	private LocalDateTime swipedAt = LocalDateTime.now();
}
