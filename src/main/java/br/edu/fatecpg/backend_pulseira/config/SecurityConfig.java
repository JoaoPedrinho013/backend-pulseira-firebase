package br.edu.fatecpg.backend_pulseira.config;

import br.edu.fatecpg.backend_pulseira.security.FirebaseBasicAuthenticationProvider;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${zelo.cors.origins}") List<String> origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins.stream().map(String::trim).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, FirebaseBasicAuthenticationProvider provider) throws Exception {
        return http.cors(Customizer.withDefaults()).authenticationProvider(provider)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/cadastro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/telemetrias").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pessoas-monitoradas").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pulseiras").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/pulseiras/*/vinculo").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pulseiras/*/alarme").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, error) -> {
                    response.setStatus(401); response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"status\":401,\"erro\":\"E-mail ou senha incorretos.\"}");
                })).build();
    }
}
