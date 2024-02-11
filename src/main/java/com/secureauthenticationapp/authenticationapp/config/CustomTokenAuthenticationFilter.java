package com.secureauthenticationapp.authenticationapp.config;

import com.secureauthenticationapp.authenticationapp.domain.exception.TokenValidationException;
import com.secureauthenticationapp.authenticationapp.domain.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Service
public class CustomTokenAuthenticationFilter extends GenericFilterBean {

    private final UserService userService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public CustomTokenAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String[] excludePaths = {"/api/v1/users/**"};

        boolean skip = false;
        for (String path : excludePaths) {
            if (pathMatcher.match(path, httpServletRequest.getServletPath())) {
                skip = true;
                break;
            }
        }

        if (!skip) {
            String header = httpServletRequest.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                try {
                    if (userService.isTokenValid(token)) {
                        String username = userService.getTokenStore().get(token);
                        if (username != null) {
                            Authentication authentication = new CustomTokenAuthentication(token, username, null);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } else {
                            throw new TokenValidationException("No user found for this token.");
                        }
                    } else {
                        throw new TokenValidationException("Invalid or expired token.");
                    }
                } catch (TokenValidationException e) {
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpServletResponse.setContentType("application/json");
                    httpServletResponse.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

