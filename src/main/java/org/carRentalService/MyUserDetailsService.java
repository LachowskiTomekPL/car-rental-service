package org.carRentalService;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * MyUserDetailsService - Serwis ładujący dane użytkownika
 *
 * To jest "baza danych użytkowników" (w tym przypadku hardcoded).
 * W prawdziwej aplikacji tutaj byłoby:
 *   userRepository.findByUsername(userName)
 *
 * Spring Security wywołuje loadUserByUsername() gdy użytkownik się loguje.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    /**
     * Ładuje użytkownika po username
     *
     * UWAGA: Hasło musi być ZAHASHOWANE BCryptem!
     * Hash "$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa" = "user"
     *
     * Możesz wygenerować hash używając:
     *   BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
     *   System.out.println(encoder.encode("user"));
     *
     * W prawdziwej aplikacji hashe są w bazie danych, a nie w kodzie!
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // BCrypt hash dla hasła "user"
        String hashedPassword = "$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa";

        return new User("user", hashedPassword, Collections.emptyList());
    }
}
