package com.example.app.config.security;

import com.example.app.model.Usuario;
import com.example.app.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("🔒 JwtFilter se está ejecutando para la ruta: " + path);

     // Rutas públicas que no requieren autenticación
        if (path.equals("/auth/login") ||
            path.equals("/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            System.out.println(token);

            System.out.println("Email extraído del token: " + email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Usuario usuario = usuarioRepository.buscarPorEmail(email).orElse(null);

                if (usuario != null && jwtUtil.isTokenValid(token, email)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(usuario, null, null);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("✅ Usuario autenticado correctamente: " + usuario.getEmail());
                } else {
                    System.out.println("❌ Token inválido o usuario no encontrado");
                }
            }
        } else {
            System.out.println("⚠️ No se encontró cabecera Authorization válida");
        }

        filterChain.doFilter(request, response);
    }
}
