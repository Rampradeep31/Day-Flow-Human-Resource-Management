package com.dayflow.hrms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility methods for accessing the Spring Security context and authenticated principal.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Gets the currently authenticated UserPrincipal if available.
     *
     * @return Optional containing the UserPrincipal or empty
     */
    public static Optional<UserPrincipal> getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    /**
     * Gets the currently authenticated user's ID.
     *
     * @return Optional containing the UUID user ID or empty
     */
    public static Optional<UUID> getCurrentUserId() {
        return getCurrentUserPrincipal().map(UserPrincipal::getId);
    }

    /**
     * Gets the currently authenticated user's email.
     *
     * @return Optional containing the email string or empty
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUserPrincipal().map(UserPrincipal::getEmail);
    }
}
