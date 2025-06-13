package com.streamflix.api.config;

import com.streamflix.api.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Verificar se header Authorization existe e começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrair token do header
        jwt = authHeader.substring(7);

        try {
            // Extrair username do token
            username = jwtService.extractUsername(jwt);

            // Se username existe e usuário não está autenticado ainda
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Buscar usuário no banco de dados
                var usuario = usuarioService.getByLoginAndAtivo(username);

                if (usuario.isPresent()) {
                    // Validar token
                    if (jwtService.isTokenValid(jwt, username)) {

                        // Criar authorities baseado no perfil do usuário
                        String perfil = "ROLE_" + usuario.get().getPerfil().name();
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority(perfil)
                        );

                        // Criar authentication token
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                        );

                        // Adicionar detalhes da requisição
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Definir authentication no SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            // Log do erro (opcional)
            logger.error("Erro ao processar token JWT: " + e.getMessage());
        }

        // Continuar a cadeia de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Método para pular filtro em rotas específicas (se necessário)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Pular filtro para rotas públicas
        return path.startsWith("/auth/") ||
                path.startsWith("/h2-console/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.equals("/");
    }
}