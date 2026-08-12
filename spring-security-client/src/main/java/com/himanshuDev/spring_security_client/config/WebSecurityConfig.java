package com.himanshuDev.spring_security_client.config;

import com.himanshuDev.spring_security_client.registration.RegistrationConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] WHITE_LIST_LOGIN_URL = {
            RegistrationConstants.VERIFY_REGISTRATION,
            RegistrationConstants.REGISTER_URL,
            RegistrationConstants.RESEND_VERIFY_TOKEN,
            RegistrationConstants.RESET_PASSWORD_LINK,
            RegistrationConstants.SAVE_PASSWORD,
            RegistrationConstants.CHANGE_PASSWORD,
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)  {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_LOGIN_URL).permitAll()
                        .anyRequest().authenticated()
                ).formLogin(AbstractAuthenticationFilterConfigurer::permitAll);

        return http.build();
    }
}
