package org.example.springmvc.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final CustomUserDetailsService userDetailsService;

    public SecurityUtils(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void refreshAuthentication(String email) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null && currentAuth.getName().equals(email)) {
            UserDetails updatedUser = userDetailsService.loadUserByUsername(email);

            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUser,
                    currentAuth.getCredentials(),
                    updatedUser.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}