package com.desofs.backend.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RemoteIpFilter extends OncePerRequestFilter {

    private static final ThreadLocal<String> ipThreadLocal = new ThreadLocal<>();

    public static String getClientIp() {
        return ipThreadLocal.get();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String ipAddress = request.getRemoteAddr();
        ipThreadLocal.set(ipAddress);

        try {
            filterChain.doFilter(request, response);
        } finally {
            ipThreadLocal.remove();
        }
    }

}
