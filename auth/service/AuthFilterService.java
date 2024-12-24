package com.spring.auth.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthFilterService extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthFilterService.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());

        try {
            System.out.println("------ Filter the Token ------");

            String jwt = parseJwt(request);

            System.out.println("Token : " + jwt);

            if (jwt != null && jwtService.validateJwtToken(jwt)) {

                System.out.println("----- TRUE CONDITION IN FILTER -----");

                String username = jwtService.getUserNameFromJwtToken(jwt);

                System.out.println("username : " + username);

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                System.out.println("user Details : " + userDetails);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                System.out.println("Username pass auth token : " + auth);

                logger.debug("Roles from JWT: {}", userDetails.getAuthorities());


                auth.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            System.out.println("----- FALSE CONDITION IN FILTER -----");
        }
        catch (Exception e) {
            System.out.println("----- CATCH FILTER -----");
            logger.error("Cannot set user authentication: {}", e.getMessage());;
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtService.getJwtFromHeader(request);
        logger.debug("AuthFilterService.java: {}", jwt);
        System.out.println("ParseJwt: " + jwt);

        return jwt;
    }
}
