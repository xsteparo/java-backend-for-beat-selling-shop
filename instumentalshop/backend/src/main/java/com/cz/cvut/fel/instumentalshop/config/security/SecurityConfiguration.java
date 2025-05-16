package com.cz.cvut.fel.instumentalshop.config.security;

import com.cz.cvut.fel.instumentalshop.service.UserService;
import com.cz.cvut.fel.instumentalshop.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userService;  // <-- сюда

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1) выключаем CSRF для REST
                .csrf(AbstractHttpConfigurer::disable)

                // 2) подключаем CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3) правила доступа
                .authorizeHttpRequests(req -> req

                        // а) регистрации — только анонимные
                        .requestMatchers("/api/v1/customers/register",
                                "/api/v1/producers/register")
                        .anonymous()

                        // б) ПУБЛИЧНЫЙ стрим mp3
                        //    AntPath: * = один сегмент; ** = любое кол-во сегментов
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/tracks/*/stream")
                        .permitAll()

                        // в) публичные GET-ы, которые у вас были
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/producers",
                                "/api/v1/producers/*",
                                "/api/v1/tracks",
                                "/api/v1/tracks/*",
                                "/api/v1/tracks/by-producer/*",
                                "/api/v1/tracks/*/licence-templates",
                                "/api/v1/tracks/*/licence-templates/*")
                        .permitAll()

                        // г) auth-эндпоинты остаются открытыми
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // д) статика / сокеты
                        .requestMatchers("/uploads/**", "/ws/**").permitAll()

                        // е) остальное — только с токеном
                        .anyRequest().authenticated()
                )

                // 4) stateless-режим (JWT)
                .sessionManagement(mgr -> mgr
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5) цепочка фильтров
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** игнорируем прямой доступ к загруженным файлам (если нужно) */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/uploads/**");
    }

    /** CORS: фронт крутится на http://localhost:5173 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /** DAO-провайдер + BCrypt */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}