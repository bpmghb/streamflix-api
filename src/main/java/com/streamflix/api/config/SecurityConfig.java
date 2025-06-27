package com.streamflix.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilitar CSRF (não necessário para APIs stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar autorização de requisições
                .authorizeHttpRequests(authz -> authz
                        // Rotas públicas (sem autenticação)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // SWAGGER - LIBERAR TODAS AS ROTAS
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        .requestMatchers("/").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Rotas que usuários comuns podem acessar
                        .requestMatchers("/api/filmes/ativos/**").hasAnyRole("USUARIO", "ADMINISTRADOR")
                        .requestMatchers("/api/filmes/{id}/detalhes").hasAnyRole("USUARIO", "ADMINISTRADOR")
                        .requestMatchers("/api/filmes/ranking/**").hasAnyRole("USUARIO", "ADMINISTRADOR")
                        .requestMatchers("/api/filmes/buscar").hasAnyRole("USUARIO", "ADMINISTRADOR")
                        .requestMatchers("/api/avaliacoes/**").hasAnyRole("USUARIO", "ADMINISTRADOR")
                        .requestMatchers("/api/listas-favoritos/**").hasAnyRole("USUARIO", "ADMINISTRADOR")
                        .requestMatchers("/api/dashboard/publico").permitAll()
                        .requestMatchers("/api/dashboard/filmes/populares").permitAll()
                        .requestMatchers("/api/dashboard/estatisticas").permitAll()

                        // Rotas exclusivas para administradores
                        .requestMatchers("/api/filmes/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/usuarios/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/dashboard/admin").hasRole("ADMINISTRADOR")

                        // Endpoints de gerenciamento de filmes (CRUD completo)
                        .requestMatchers("POST", "/api/filmes").hasRole("ADMINISTRADOR")
                        .requestMatchers("PUT", "/api/filmes/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("DELETE", "/api/filmes/**").hasRole("ADMINISTRADOR")

                        // Todas as outras rotas requerem autenticação
                        .anyRequest().authenticated()
                )

                // Configurar sessão como stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Adicionar filtro JWT antes do filtro de autenticação padrão
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Configurar headers para H2 Console e Swagger
                .headers(headers -> headers
                        .frameOptions().sameOrigin() // Corrigido o deprecated
                );

        return http.build();
    }
}