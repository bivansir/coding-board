package ru.coding_board.session_service.common.debug;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jboss.logging.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_MDC_KEY = "requestId";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    private static final List<String> IGNORED_PATHS = List.of(
        "/swagger-ui", "/swagger-ui.html", "/v3/api-docs",
         "/swagger-resources", "/webjars", "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean shouldLog = IGNORED_PATHS.stream().noneMatch(path::startsWith);

        String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
            .filter(id -> !id.isBlank())
            .orElse(UUID.randomUUID().toString());

        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        if (shouldLog) {
            log.info("Request started: {} {}", request.getMethod(), request.getRequestURI());
        }

        try {
            chain.doFilter(request, response);
        } finally {
            if (shouldLog) {
                log.info("Request finished: {} {} (status: {})",
                    request.getMethod(), request.getRequestURI(), response.getStatus());
            }
            MDC.clear();
        }
    }
};
