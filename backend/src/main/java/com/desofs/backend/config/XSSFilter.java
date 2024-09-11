package com.desofs.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class XSSFilter extends OncePerRequestFilter {

    @Value("${xss.endpoints-to-ignore}")
    private Set<String> endpointToIgnoreXSS;

    @Value("${xss.fields-to-ignore}")
    private Set<String> fieldsToIgnore;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        HttpServletRequest httpRequest = request;
        String requestURI = httpRequest.getRequestURI();

        if (endpointToIgnoreXSS.stream().noneMatch(requestURI::contains)) {
            request = new XSSRequestWrapper(httpRequest, fieldsToIgnore);
        }
        filterChain.doFilter(request, response);
    }
}