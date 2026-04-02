package com.catinder.service;

import com.catinder.dto.AuthRequest;
import com.catinder.dto.AuthResponse;
import com.catinder.model.User;
import com.catinder.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Service d'authentification: gère inscription/connexion et émission d'un JWT signé.
@Service
@Transactional
public class AuthService {

    private static final String HMAC_ALGO = "HmacSHA256";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${catinder.jwt.secret:ChangeMeSuperSecretKey}")
    private String jwtSecret;

    @Value("${catinder.jwt.expiration-seconds:86400}")
    private long jwtExpirationSeconds;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Inscription d'un nouvel utilisateur avec contrôle d'unicité login/email.
    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByLogin(request.login())) {
            throw new IllegalArgumentException("Ce login est déjà utilisé.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        User user = User.builder()
            .login(request.login().trim())
            .email(request.email().trim().toLowerCase())
            .passwordHash(passwordEncoder.encode(request.password()))
            .build();

        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    // Connexion d'un utilisateur (login OU email + mot de passe).
    public AuthResponse login(AuthRequest request) {
        String normalizedEmail = request.email() == null ? "" : request.email().trim().toLowerCase();
        String normalizedLogin = request.login() == null ? "" : request.login().trim();

        User user = userRepository.findByEmail(normalizedEmail)
            .or(() -> userRepository.findByLogin(normalizedLogin))
            .orElseThrow(() -> new BadCredentialsException("Identifiants invalides."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Identifiants invalides.");
        }

        return buildAuthResponse(user);
    }

    // Vérifie la validité du token (signature + expiration + sujet).
    public boolean isTokenValid(String token, String expectedLogin) {
        try {
            String payload = decodeAndVerify(token);
            String subject = extractStringClaim(payload, "sub");
            long exp = extractLongClaim(payload, "exp");
            long now = Instant.now().getEpochSecond();
            return expectedLogin.equals(subject) && exp > now;
        } catch (Exception ex) {
            return false;
        }
    }

    // Extrait le login (claim sub) depuis un token valide.
    public String extractLogin(String token) {
        try {
            String payload = decodeAndVerify(token);
            return extractStringClaim(payload, "sub");
        } catch (Exception ex) {
            return null;
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = generateToken(user);
        return new AuthResponse(token, "Bearer", user.getId(), user.getLogin(), user.getEmail());
    }

    private String generateToken(User user) {
        long now = Instant.now().getEpochSecond();
        long exp = now + jwtExpirationSeconds;

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{"
            + "\"sub\":\"" + jsonEscape(user.getLogin()) + "\"," 
            + "\"uid\":" + user.getId() + ","
            + "\"email\":\"" + jsonEscape(user.getEmail()) + "\"," 
            + "\"iat\":" + now + ","
            + "\"exp\":" + exp
            + "}";

        String encodedHeader = base64Url(headerJson);
        String encodedPayload = base64Url(payloadJson);
        String unsignedToken = encodedHeader + "." + encodedPayload;
        String signature = hmacSha256Base64Url(unsignedToken, jwtSecret);

        return unsignedToken + "." + signature;
    }

    private String decodeAndVerify(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("JWT invalide.");
        }

        String unsignedToken = parts[0] + "." + parts[1];
        String expectedSignature = hmacSha256Base64Url(unsignedToken, jwtSecret);
        if (!expectedSignature.equals(parts[2])) {
            throw new IllegalArgumentException("Signature JWT invalide.");
        }

        return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
    }

    private String base64Url(String text) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private String extractStringClaim(String payloadJson, String claim) {
        String marker = "\"" + claim + "\":\"";
        int start = payloadJson.indexOf(marker);
        if (start < 0) {
            throw new IllegalArgumentException("Claim manquante: " + claim);
        }
        int valueStart = start + marker.length();
        int valueEnd = payloadJson.indexOf('"', valueStart);
        if (valueEnd < 0) {
            throw new IllegalArgumentException("Claim string invalide: " + claim);
        }
        return payloadJson.substring(valueStart, valueEnd);
    }

    private long extractLongClaim(String payloadJson, String claim) {
        String marker = "\"" + claim + "\":";
        int start = payloadJson.indexOf(marker);
        if (start < 0) {
            throw new IllegalArgumentException("Claim manquante: " + claim);
        }
        int valueStart = start + marker.length();
        int valueEnd = valueStart;
        while (valueEnd < payloadJson.length() && Character.isDigit(payloadJson.charAt(valueEnd))) {
            valueEnd++;
        }
        if (valueEnd == valueStart) {
            throw new IllegalArgumentException("Claim numérique invalide: " + claim);
        }
        return Long.parseLong(payloadJson.substring(valueStart, valueEnd));
    }

    private String jsonEscape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String hmacSha256Base64Url(String data, String secret) {
        try {
            Mac hmac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
            hmac.init(keySpec);
            byte[] signature = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de signer le JWT.", e);
        }
    }
}
