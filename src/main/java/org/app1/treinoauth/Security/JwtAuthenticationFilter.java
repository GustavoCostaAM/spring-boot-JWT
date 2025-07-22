package org.app1.treinoauth.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.app1.treinoauth.Model.UserPrincipal;
import org.app1.treinoauth.Service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService, UserDetailsService userDetailsService){
        this.jwtService=jwtService;
        this.customUserDetailsService=customUserDetailsService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        //pega o header de onde fica o token
        final String authHeader = request.getHeader("Authorization");

        //verifica se possui um token
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response); //não autentica o usuario com uma role
            return;
        }

        final String token = authHeader.substring(7); //extraindo token
        final String email = jwtService.extractUsername(token); //decodifica o token e procura um email

        //garante que ainda não está autenticado
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userPrincipal = customUserDetailsService.loadUserByUsername(email);

            //valida o token
            if (jwtService.validateToken(token, userPrincipal)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null, userPrincipal.getAuthorities());

                //objeto para aprovar autenticação
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                //as permissões são verificadas pelo spring, pelo authToken
            }

        }
        filterChain.doFilter(request, response);
    }
}
