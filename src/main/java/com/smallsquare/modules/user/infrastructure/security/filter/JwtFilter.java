package com.smallsquare.modules.user.infrastructure.security.filter;

import com.smallsquare.modules.user.infrastructure.jwt.JwtUtil;
import com.smallsquare.modules.user.infrastructure.security.model.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);


            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserId(token);
                String username = jwtUtil.getUsername(token);
                String role = jwtUtil.getRole(token); // "ROLE_USER", "ROLE_ADMIN" 등

                CustomUserDetails userDetails = new CustomUserDetails(userId, username, role);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}