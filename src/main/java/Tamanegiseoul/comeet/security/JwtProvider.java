package Tamanegiseoul.comeet.security;

import Tamanegiseoul.comeet.domain.Member;
import Tamanegiseoul.comeet.domain.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;
    private Algorithm algorithm;
    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds; // default set as 60min

    public JwtProvider(@Value("${security.jwt.token.secret-key}") String secretKey, @Value("${security.jwt.token.expire-length}") Long validityInMilliseconds) {
        this.algorithm = Algorithm.HMAC256(secretKey.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
    }

    // generate token
    public String generateAccessToken(Member member) {
        return JWT.create()
                .withSubject(member.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis()+ (validityInMilliseconds / 60))) // 3 min
                .withClaim("roles", member.getRoles().stream().map(Role ::getRoleName).collect(Collectors.toList()))
                .withClaim("nickname", member.getNickname())
                .withClaim("memberId", member.getMemberId())
                .sign(algorithm);
    }

    public String generateRefreshToken(String email) {
        return JWT.create()
                .withSubject(email) // get email (security's username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000 )) // 24hr
                .sign(algorithm);
    }

    // JWT ???????????? ?????? ?????? ??????
    public boolean getAuthentication(String token, String email) {
        String subject = JWT.require(algorithm).build()
                .verify(token)
                .getSubject(); // extract token's subject(member email)

        return subject.equals(email);
    }

    // ???????????? ?????? email ??????
    public String getUserEmail(String token) {
        return JWT.require(algorithm).build()
                .verify(token)
                .getSubject(); // extract token's subject(member email)
    }

    // ?????????????????? ?????? ?????? ??????
    public Collection<SimpleGrantedAuthority> getAuthorities(String token) {

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);


        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();


        for(String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }


        return authorities;
    }

    public boolean validateToken(String token) throws ExpiredJwtException {
        try {
            //Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature => {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token => {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token => {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty. => {}", e.getMessage());
        }
        return false;
    }

    // Get AccessToken from request's cookie
    public String getAccessToken(HttpServletRequest request) {
        return request.getCookies()[0].getValue();
    }

    // Get RefreshToken from request's cookie
    public String getRefreshToken(HttpServletRequest request) {
        return request.getCookies()[1].getValue();
    }

}
