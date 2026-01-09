package org.carRentalService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtUtil - Narzędzie do pracy z JWT (JSON Web Tokens)
 *
 * JWT to "bezpieczny kupon" dla użytkownika:
 * - Użytkownik loguje się (username + password)
 * - Server generuje JWT i wysyła użytkownikowi
 * - Użytkownik wysyła JWT przy każdym requeście
 * - Server sprawdza JWT (czy ważny, nie zmodyfikowany, nie wygasł)
 *
 * JWT składa się z 3 części:
 *   HEADER.PAYLOAD.SIGNATURE
 *   eyJhbG...  .eyJzdWI...  .SflKxw...
 *
 * UWAGA: To jest JJWT 0.12 API (całkowicie różne od 0.9.1!)
 */
@Service
public class JwtUtil {

    /**
     * SECRET_KEY - Klucz do podpisywania tokenów
     *
     * WAŻNE:
     * - W JJWT 0.12 musi być SecretKey (nie String!)
     * - Musi mieć minimum 256 bitów (32 bajty) dla HS256
     * - W produkcji NIGDY nie hardcode, użyj zmiennych środowiskowych!
     *
     * Keys.hmacShaKeyFor() - tworzy SecretKey z byte array
     * "my-secret-key-for-jwt-token-signing-min-32-chars!" - 50 znaków = 400 bitów ✅
     */
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "my-secret-key-for-jwt-token-signing-min-32-chars!".getBytes()
    );

    /**
     * Wyciąga username z tokena
     *
     * JWT payload zawiera "claims" (pola):
     *   {
     *     "sub": "user",        ← subject = username
     *     "iat": 1516239022,    ← issued at
     *     "exp": 1516242622     ← expiration
     *   }
     *
     * Claims.getSubject() zwraca "sub" field
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Wyciąga datę wygaśnięcia z tokena
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Wyciąga konkretny claim (pole) z tokena
     *
     * Generic method - możesz wyciągnąć dowolne pole:
     *   extractClaim(token, Claims::getSubject)    → String
     *   extractClaim(token, Claims::getExpiration) → Date
     *   extractClaim(token, Claims::getIssuedAt)   → Date
     *
     * Function<Claims, T> claimsResolver - lambda wyciągająca konkretne pole
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Wyciąga wszystkie claims z tokena (dekoduje JWT)
     *
     * NOWE API w JJWT 0.12:
     * - Jwts.parser() → tworzy parser
     * - .verifyWith(SECRET_KEY) → ustaw klucz do weryfikacji podpisu (było: setSigningKey)
     * - .build() → zbuduj parser
     * - .parseSignedClaims(token) → parsuj token (było: parseClaimsJws)
     * - .getPayload() → pobierz claims (było: getBody)
     *
     * Rzuca wyjątek jeśli:
     * - Token jest zmodyfikowany (zły podpis)
     * - Token wygasł
     * - Token jest malformed (złe formatowanie)
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)          // Ustaw klucz do weryfikacji
                .build()                         // Zbuduj parser
                .parseSignedClaims(token)        // Parsuj token
                .getPayload();                   // Pobierz payload (claims)
    }

    /**
     * Sprawdza czy token wygasł
     *
     * Porównuje expiration date z teraz
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generuje token dla użytkownika
     *
     * Tworzy JWT zawierający:
     * - subject (username)
     * - issued at (kiedy utworzony)
     * - expiration (kiedy wygasa)
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Tworzy token JWT
     *
     * NOWE API w JJWT 0.12:
     * - .claims(claims) → dodaj custom claims (było: setClaims)
     * - .subject(subject) → ustaw subject/username (było: setSubject)
     * - .issuedAt(date) → kiedy utworzony (było: setIssuedAt)
     * - .expiration(date) → kiedy wygasa (było: setExpiration)
     * - .signWith(SECRET_KEY) → podpisz tokenem (było: signWith(algorithm, key))
     *
     * UWAGA: Expiration = 20000ms = 20 sekund (dla testów!)
     * W produkcji użyj 1h - 24h:
     *   new Date(System.currentTimeMillis() + 3600000) // 1 godzina
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)                                          // Custom claims (puste)
                .subject(subject)                                        // Username
                .issuedAt(new Date(System.currentTimeMillis()))         // Teraz
                .expiration(new Date(System.currentTimeMillis() + 20000)) // +20 sekund
                .signWith(SECRET_KEY)                                    // Podpisz kluczem
                .compact();                                              // Zbuduj string
    }

    /**
     * Waliduje token
     *
     * Sprawdza:
     * 1. Czy username w tokenie = username użytkownika
     * 2. Czy token nie wygasł
     *
     * Podpis jest już sprawdzany w extractAllClaims()
     * (rzuci wyjątek jeśli zły)
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
