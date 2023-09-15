package com.coursework.Server.Config;

import com.coursework.Server.Model.User;
import com.coursework.Server.Repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo){
        return username -> {
            User user = userRepo.findByUsername(username);

            if(user != null) return user;

            throw new UsernameNotFoundException("User ‘" + username + "’ not found");
        };
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                    .formLogin()
                    .loginPage("/login")
                .and()
                    .authorizeHttpRequests()
                    .requestMatchers("/", "/home", "/services/**", "/analytics/**", "/contacts", "/profile").authenticated()
                    .requestMatchers("/administration/users/**").hasRole("ADMIN")
                    .requestMatchers("/**").permitAll()
                .and()
                    .logout()
                    .logoutSuccessUrl("/login")
                .and()
                    .logout()
                .and()
                    .build();

    }
}