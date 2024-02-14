package com.example.LoginTask.auth;

import com.example.LoginTask.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil tokenUtil;
    private final UserService userService;

    public JwtTokenFilter(JwtTokenUtil tokenUtil, UserService userService) {
        this.tokenUtil = tokenUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String userName = null;
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
             token = authorizationHeader.substring(7);
            try {
                userName = tokenUtil.getUsernameFromToken(token);
            }  catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the username");
                logger.error(e.getStackTrace());
            } catch (ExpiredJwtException e) {
                logger.info("Given jwt token is expired");
                logger.error(e.getStackTrace());
            } catch (MalformedJwtException e) {
                logger.info("Some changed has done in token: Invalid Token");
                logger.error(e.getStackTrace());
            } catch (Exception e) {
                logger.error(e.getStackTrace());
            }
        }else{
            logger.info("Invalid header value");
        }
        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails user = userService.getUserDetailsByName(userName);
            boolean validToken = tokenUtil.validateToken(token, user);
            if(validToken){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }else{
            logger.info("Validation failed");
        }

        filterChain.doFilter(request, response);
    }
}
