package org.carRentalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Konfiguracja Spring Security - "System ochrony" aplikacji
 *
 * Odpowiada za:
 * - Autoryzację (kto ma dostęp do jakich endpointów)
 * - Uwierzytelnianie (sprawdzanie username/password)
 * - Zarządzanie JWT tokenami
 * - Konfigurację filtrów (JwtRequestFilter)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * SecurityFilterChain - "Łańcuch filtrów bezpieczeństwa"
     *
     * To jest GŁÓWNA konfiguracja Spring Security.
     * Definiuje:
     * 1. Które endpointy są publiczne, które wymagają logowania
     * 2. Jak Spring Security ma zarządzać sesjami (STATELESS = JWT, bez sesji)
     * 3. Jakie filtry mają być użyte (nasz JwtRequestFilter)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF (Cross-Site Request Forgery) - wyłączamy bo używamy JWT
            // CSRF chroni przed atakami gdy używamy cookies/sesji
            // JWT w headerze Authorization nie potrzebuje ochrony CSRF
            .csrf(csrf -> csrf.disable())

            // Autoryzacja requestów - "Kto może gdzie wchodzić"
            .authorizeHttpRequests(auth -> auth
                // .requestMatchers() - Spring Security 6 (nowe API)
                // .antMatchers() - Spring Security 5 (stare API, USUNIĘTE)

                .requestMatchers("/auth").permitAll()  // /auth dostępny dla WSZYSTKICH (publiczny)
                .anyRequest().authenticated()           // Wszystkie inne endpointy wymagają logowania
            )

            // Zarządzanie sesjami - STATELESS = bez sesji, tylko JWT
            // Server NIE przechowuje informacji o zalogowanych użytkownikach
            // Każdy request musi zawierać token JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Dodanie naszego JWT filtra PRZED standardowym filtrem Spring Security
            // Kolejność: JwtRequestFilter → UsernamePasswordAuthenticationFilter → Controller
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationProvider - "Dostawca uwierzytelniania"
     *
     * Odpowiada za sprawdzanie username + password.
     * DaoAuthenticationProvider = DAO (Data Access Object) - pobiera użytkownika z bazy/serwisu
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        // UserDetailsService - serwis ładujący dane użytkownika (MyUserDetailsService)
        provider.setUserDetailsService(myUserDetailsService);

        // PasswordEncoder - jak sprawdzać hasła (BCrypt = bezpieczne hashowanie)
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * AuthenticationManager - "Menadżer uwierzytelniania"
     *
     * To jest główny "zarządca" autoryzacji w Spring Security.
     * Używany w CarsController przy logowaniu:
     *   authenticationManager.authenticate(username, password)
     *
     * W Spring Security 6 tworzymy go z AuthenticationConfiguration
     * (w Security 5 było: super.authenticationManagerBean())
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * PasswordEncoder - "Enkoder haseł"
     *
     * WAŻNE: NIGDY nie przechowuj haseł w plain text!
     *
     * BCryptPasswordEncoder:
     * - Hashuje hasła używając algorytmu BCrypt (bardzo bezpieczny)
     * - Automatycznie dodaje "salt" (losowy string zapobiegający rainbow table attacks)
     * - Jednorazowe hashowanie (nie da się odtworzyć hasła z hasha)
     *
     * Przykład:
     *   password: "user"
     *   hash:     "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     *
     * UWAGA: Zmieniamy z NoOpPasswordEncoder (DEPRECATED, NIEBEZPIECZNY)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
