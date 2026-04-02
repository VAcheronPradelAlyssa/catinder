package com.catinder.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

// Entité Cat : représente un chat à swiper
@Entity
@Table(name = "cat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 2, max = 32)
	@Column(nullable = false)
	private String name;

	@Size(max = 255)
	private String bio;

	@Column(name = "image_url")
	private String imageUrl;

	// Un chat peut être swipé plusieurs fois
	@OneToMany(mappedBy = "cat", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Swipe> swipes;
}
