package com.catinder.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

// Entité User : représente un utilisateur Catinder
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 3, max = 32)
	@Column(unique = true, nullable = false)
	private String login;

	@NotBlank
	@Email
	@Column(unique = true, nullable = false)
	private String email;

	@NotBlank
	@Size(min = 8)
	@Column(nullable = false)
	private String passwordHash;

	// Un utilisateur peut swiper plusieurs chats
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Swipe> swipes;
}
