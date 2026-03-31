package com.example.hrms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.security.demo-users.admin.username:admin}")
    private String adminUsername;

    @Value("${app.security.demo-users.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.security.demo-users.hr.username:hr}")
    private String hrUsername;

    @Value("${app.security.demo-users.hr.password:hr123}")
    private String hrPassword;

    @Value("${app.security.demo-users.employee.username:employee}")
    private String employeeUsername;

    @Value("${app.security.demo-users.employee.password:emp123}")
    private String employeePassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/v1", "/api/v1/info").permitAll()
                        .requestMatchers("/api/v1/payroll/**").hasAnyRole("ADMIN", "HR")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var admin = User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();
        var hr = User.withUsername(hrUsername)
                .password(passwordEncoder.encode(hrPassword))
                .roles("HR")
                .build();
        var employee = User.withUsername(employeeUsername)
                .password(passwordEncoder.encode(employeePassword))
                .roles("EMPLOYEE")
                .build();
        return new InMemoryUserDetailsManager(admin, hr, employee);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
