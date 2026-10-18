package com.tienda.infrastructure;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.Customizer;
@Configuration
public class SecurityConfig {
 @Bean UserDetailsService users(@Value("${tienda.admin.password}") String password) {
  return new InMemoryUserDetailsManager(User.withUsername("admin").password(new BCryptPasswordEncoder().encode(password)).roles("ADMIN").build());
 }
 @Bean org.springframework.security.crypto.password.PasswordEncoder encoder() {return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
  return http.csrf(c->c.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
   .authorizeHttpRequests(a->a.requestMatchers(org.springframework.http.HttpMethod.GET,"/**").permitAll()
    .requestMatchers("/api/products/**").hasRole("ADMIN").anyRequest().permitAll())
   .httpBasic(Customizer.withDefaults()).build();
 }
}
