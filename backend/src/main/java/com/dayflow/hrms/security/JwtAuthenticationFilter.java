package com.dayflow.hrms.security;

import com.dayflow.hrms.entity.User;
import com.dayflow.hrms.entity.UserStatus;
import com.dayflow.hrms.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Filter that intercepts incoming HTTP requests to validate Supabase JWT tokens
 * and authenticate the user in Spring Security Context with their roles.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtService.isTokenValid(jwt)) {
                UUID userId = jwtService.extractUserId(jwt);
                String email = jwtService.extractEmail(jwt);

                Optional<User> userOptional = Optional.empty();

                if (userId != null) {
                    userOptional = userRepository.findByIdWithRoles(userId);
                }
                if (userOptional.isEmpty() && StringUtils.hasText(email)) {
                    userOptional = userRepository.findByEmailWithRoles(email);
                }

                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    if (user.getStatus() == UserStatus.ACTIVE) {
                        UserPrincipal principal = UserPrincipal.create(user);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        principal,
                                        null,
                                        principal.getAuthorities()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Successfully authenticated user: {} with roles: {}", user.getEmail(), principal.getRoleNames());
                    } else {
                        log.warn("User {} is inactive. Authentication rejected.", user.getEmail());
                    }
                } else {
                    log.debug("No local user record found matching token identity (sub: {}, email: {})", userId, email);
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
