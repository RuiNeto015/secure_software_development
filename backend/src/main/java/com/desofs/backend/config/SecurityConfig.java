package com.desofs.backend.config;

import com.desofs.backend.domain.enums.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RemoteIpFilter ipFilter;
    private final XSSFilter xssFilter;
    private final RequestSizeFilter requestSizeFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Value("${springdoc.api-docs.path}")
    private String restApiDocPath;

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        String rentalPropertyEndpoint = "/rental_property/{id}";

        http.cors(corsSpec -> {
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.setAllowCredentials(true);
            corsConfig.setAllowedOrigins(List.of("http://localhost:4200"));
            List.of("*").forEach(corsConfig::addAllowedMethod);
            List.of("*").forEach(corsConfig::addAllowedHeader);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfig);
            corsSpec.configurationSource(source);
        });

        http.csrf(AbstractHttpConfigurer::disable);

        http.headers(headers -> {
            headers.xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));
            headers.contentSecurityPolicy(cps -> cps.policyDirectives("script-src 'self'"));
        });

        http.sessionManagement(sessionSpec -> sessionSpec.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(exceptions -> {
            exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
            exceptions.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
        });

        http.addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(requestSizeFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(ipFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(xssFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers(HttpMethod.POST, "/auth/login/generateOTP").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/forgot-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/rental_property/all").permitAll()
                .requestMatchers(HttpMethod.POST, rentalPropertyEndpoint).permitAll()
                .requestMatchers(HttpMethod.POST, "/rental_property/create").hasAuthority(Authority.PROPERTYOWNER)
                .requestMatchers(HttpMethod.GET, rentalPropertyEndpoint).permitAll()
                .requestMatchers(HttpMethod.POST, "/rental_property/create").hasAuthority(Authority.PROPERTYOWNER)
                .requestMatchers(HttpMethod.GET, "/rental_property/allByUser/{id}").authenticated()
                .requestMatchers(HttpMethod.DELETE, rentalPropertyEndpoint).hasAnyAuthority(Authority.PROPERTYOWNER, Authority.BUSINESSADMIN)
                .requestMatchers(HttpMethod.PUT, rentalPropertyEndpoint).hasAnyAuthority(Authority.PROPERTYOWNER, Authority.BUSINESSADMIN)
                .requestMatchers(HttpMethod.POST, "/booking/add").permitAll()
                .requestMatchers(HttpMethod.POST, "/booking/stripe-webhook").permitAll()
                .requestMatchers(HttpMethod.POST, "/booking/{id}").authenticated()
                .requestMatchers(HttpMethod.POST, "/booking/getAllByUser/{id}").authenticated()
                .requestMatchers(HttpMethod.PUT, "/booking/{id}/cancel").authenticated()
                .requestMatchers(HttpMethod.POST, "/review/add").authenticated()
                .requestMatchers(HttpMethod.POST, "/review/change_state").hasAuthority(Authority.BUSINESSADMIN)
                .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                .requestMatchers(restApiDocPath + "/**").permitAll()
                .requestMatchers(swaggerPath + "/**").permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}