package org.camunda.bpm.getstarted.springbootkeycloakexample.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return new GrantedAuthoritiesMapperImpl();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(request ->{
                    request.requestMatchers("/api/public/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/secure/**")
                            .hasRole("user")
                            .requestMatchers("/api/logout").permitAll();
                })
                .oauth2Login(oauth2 ->{
                    oauth2.userInfoEndpoint(userInfoEndpointConfig -> {
                        userInfoEndpointConfig.userAuthoritiesMapper(this.userAuthoritiesMapper());
                    });
                });

        return http.build();
    }
}

