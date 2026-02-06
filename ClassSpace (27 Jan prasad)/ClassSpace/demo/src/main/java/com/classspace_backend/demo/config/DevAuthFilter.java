package com.classspace_backend.demo.config;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Profile("dev")
public class DevAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public DevAuthFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("DevAuthFilter CONSTRUCTED");
    }
    
   



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
            
    ) throws ServletException, IOException {
    	
    	System.out.println("DevAuthFilter EXECUTED for " + request.getRequestURI());


        // Inject fake teacher ONLY if no auth exists
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            User teacher = userRepository
                    .findByEmail("teacher1@classspace.com")
                    .orElse(null);

            if (teacher != null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                teacher,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_TEACHER"))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
