package com.example.empleados.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitingFilter implements Filter {
    private static final int REQUESTS_PER_MINUTE = 10;
    private static final long MINUTE_IN_MS = 60 * 1000;

    private final ConcurrentHashMap<String, IpRateLimit> rateLimits = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIp(httpRequest);
        String endpoint = httpRequest.getRequestURI();

        if (endpoint.contains("/api/registros") && httpRequest.getMethod().equals("POST")) {
            if (!isAllowed(clientIp)) {
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\":\"Too many requests. Maximum " + REQUESTS_PER_MINUTE + " requests per minute.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isAllowed(String ip) {
        long now = System.currentTimeMillis();
        IpRateLimit rateLimit = rateLimits.computeIfAbsent(ip, k -> new IpRateLimit());

        synchronized (rateLimit) {
            if (now - rateLimit.windowStart > MINUTE_IN_MS) {
                rateLimit.windowStart = now;
                rateLimit.count = 1;
                return true;
            }

            if (rateLimit.count < REQUESTS_PER_MINUTE) {
                rateLimit.count++;
                return true;
            }

            return false;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private static class IpRateLimit {
        long windowStart = System.currentTimeMillis();
        int count = 0;
    }
}
