package com.spring.auth.config;

//import com.spring.auth.service.AuthEntryPointJwt;
import com.spring.auth.service.AuthEntryPointJwt;
import com.spring.auth.service.AuthFilterService;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity

@Configuration
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthFilterService authenticationJwtTokenFilter() {
        return new AuthFilterService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
            throws Exception {

        System.out.println("----- Default security filter config -----");

        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/e-com/v1/auth/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated());

                httpSecurity.sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler));

//        httpSecurity.authenticationProvider(authenticationProvider);

        httpSecurity.addFilterBefore(authenticationJwtTokenFilter(),
                        UsernamePasswordAuthenticationFilter.class);

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
}
