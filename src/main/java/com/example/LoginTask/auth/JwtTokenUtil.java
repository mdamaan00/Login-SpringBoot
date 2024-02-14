package com.example.LoginTask.auth;

import com.example.LoginTask.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "aXXXHa12AzcmjaAaasfvgbhnjmkloiuytreqasdfcgvhbjnADFGHKLLMBVwdfvggb234567899222kmkjhngbfvdcsoolpikujyhtgrfedsqazsxdcfvgbhnjmkllobbbpplikujyhtgfc";
    private static final long EXPIRATION_TIME =  5 * 60 * 60 * 360;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, user.getName());
    }

    private String doGenerateToken(Map<String, Object> claims, String name) {
        return Jwts.builder().setClaims(claims).setSubject(name).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String name = getUsernameFromToken(token);
        return (name.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
