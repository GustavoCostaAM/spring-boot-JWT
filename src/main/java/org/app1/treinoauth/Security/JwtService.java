package org.app1.treinoauth.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    @Value("${JWT_KEY}")
    private String key;

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()//inicia a build
                .setSubject(userDetails.getUsername())//define o contexto (foi alterado para o id)
                .setIssuedAt(new Date())//data de inicio
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))//data de fim
                .signWith(Keys.hmacShaKeyFor(key.getBytes()), SignatureAlgorithm.HS256)//assina o token
                .compact();//finaliza a build
    }

    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
