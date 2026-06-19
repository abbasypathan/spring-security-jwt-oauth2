package com.springsecurity.filter;

import com.springsecurity.constant.Constants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Here we can generate JWT token and send it to client
        // For example, we can generate a JWT token using the JJWT library and set it in the response header
        // String jwtToken = Jwts.builder().setSubject("user").signWith(SignatureAlgorithm.HS256, "secret").compact();
        // response.setHeader("Authorization", "Bearer " + jwtToken);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            Environment env = getEnvironment();
            if (null != env) {
                String secret = env.getProperty(Constants.JWT_SECRET_KEY, Constants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                String token = Jwts.builder()
                        .issuer("Eazy Bank")
                        .setSubject("JWT Token")
                        .claim("username", authentication.getName())
                        .claim("authorities", authentication.getAuthorities().stream()
                                .map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.joining()))
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 3000000)) // 50 minutes
                        .signWith(secretKey)
                        .compact();
                response.setHeader(Constants.JWT_TOKEN_HEADER, token);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/user");
    }
}
