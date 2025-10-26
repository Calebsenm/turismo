package com.app.turismo.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        // Log all POST requests to /api/hoteles
        if ("POST".equals(method) && requestURI.contains("/api/hoteles")) {
            System.out.println("=== REQUEST LOGGING ===");
            System.out.println("Method: " + method);
            System.out.println("URI: " + requestURI);
            System.out.println("Content-Type: " + request.getContentType());
            System.out.println("Authorization: " + request.getHeader("Authorization"));
            System.out.println("=======================");
        }

        filterChain.doFilter(request, response);

        // Log response status for POST requests to /api/hoteles
        if ("POST".equals(method) && requestURI.contains("/api/hoteles")) {
            System.out.println("=== RESPONSE LOGGING ===");
            System.out.println("Status: " + response.getStatus());
            System.out.println("========================");
        }
    }
}