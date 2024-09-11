package com.desofs.backend.config;

import com.desofs.backend.exceptions.RequestSizeLimitException;
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
public class RequestSizeFilter extends OncePerRequestFilter {

    @Value("${http-size-request.limit-mb}")
    private Integer maxRequestMB;

    @Value("${http-size-request.endpoints-to-ignore}")
    private Set<String> endpointsToIgnore;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        int MAX_MB_REQUEST_SIZE = maxRequestMB * 1024 * 1024;

        int contentLength = request.getContentLength();

        if (!endpointsToIgnore.contains(request.getRequestURI())) {
            if (contentLength > MAX_MB_REQUEST_SIZE) {
                throw new RequestSizeLimitException("Request size exceeds the configured maximum");
            }
        }

        filterChain.doFilter(request, response);
    }
}
