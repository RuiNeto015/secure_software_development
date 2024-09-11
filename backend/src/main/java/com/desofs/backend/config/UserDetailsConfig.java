package com.desofs.backend.config;

import com.desofs.backend.database.repositories.UserRepository;
import com.desofs.backend.domain.aggregates.UserDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

@Configuration
@RequiredArgsConstructor
public class UserDetailsConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            UserDomain user = this.userRepository.findByEmail(email);

            if (user == null) {
                throw new UsernameNotFoundException(format("User: %s not found", email));
            }

            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

            return new User(user.getId().value(), user.getPassword().value(), Collections.singletonList(authority));
        };
    }

    public static boolean hasAuthorization(Authentication authentication, String authority) {
        List<String> authRoleList = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return authRoleList.contains(authority);
    }
}
