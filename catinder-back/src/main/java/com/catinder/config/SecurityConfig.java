package com.catinder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Configuration de la sécurité : JWT, CORS, Bcrypt, endpoints publics/privés
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// Configuration du password encoder (Bcrypt)
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Configuration CORS pour autoriser le front Angular (localhost:4200)
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:4200"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	// Chaîne de filtres de sécurité (stateless, JWT, CORS, endpoints publics)
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(null) // Active CORS
			.and()
			.csrf().disable() // Pas de CSRF pour API stateless
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/**").permitAll() // Auth endpoints publics
				.anyRequest().authenticated() // Tout le reste nécessite un JWT
			);
		// TODO: Ajouter le filtre JWT ici (ex: http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);)
		return http.build();
	}
}
