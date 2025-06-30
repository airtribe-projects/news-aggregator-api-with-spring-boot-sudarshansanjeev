package org.airtribe.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUserName(token);
        }

        // validate token and set authentication in context if not already set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // load user details from database using our custom service
            var userdetails = userDetailsService.loadUserByUsername(username);

            // check if token is valid (signature, expiration, etc)
            if (jwtService.validateToken(token)) {
                var authtoken = new UsernamePasswordAuthenticationToken(
                        userdetails,
                        null, // credentials are already validated via token
                        userdetails.getAuthorities()
                );

                // Add additional request details (IP address, session info, etc)
                authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication to Security Context so Spring knows the user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authtoken);

            }
        }


        //Continue the rest of the filter chain
        filterChain.doFilter(request, response);

    }
}
