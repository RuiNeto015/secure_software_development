package com.desofs.backend.interceptors;

import com.desofs.backend.services.RateService;
import com.stripe.exception.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final JwtDecoder jwtDecoder;
    private final RateService rateService;

    @Value("${rate_limit}")
    private int RATE_LIMIT;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
            throws Exception {
        String rawToken = request.getHeader("Authorization");
        if (rawToken != null) {
            Jwt token = jwtDecoder.decode(rawToken.substring(7));
            String user = token.getClaim("email");
            int rate = rateService.getUserRate(user);

            if (rate < RATE_LIMIT) {
                rateService.increaseRate(user);
                return true;
            }

            String message = String.format("User %s has exceeded his request rate %d", user, RATE_LIMIT);
            String param = "rateLimit";
            String requestId = request.getRequestURI();
            String code = "429";
            Integer statusCode = HttpStatus.TOO_MANY_REQUESTS.value();
            throw new RateLimitException(message, param, requestId, code, statusCode, null);
        }
        return true;
    }
}