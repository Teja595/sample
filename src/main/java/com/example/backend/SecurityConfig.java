// package com.example.backend;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.AuthenticationFailureHandler;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// @Configuration
// public class SecurityConfig {

//     private final UserDetailsService userDetailsService;

//     public SecurityConfig(UserDetailsService userDetailsService) {
//         this.userDetailsService = userDetailsService;
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf().disable()
//             .cors() // Enable CORS
//             .and()
//             .authorizeHttpRequests(authorizeRequests ->
//                 authorizeRequests
//                     .requestMatchers("/x/login", "/x/public/**").permitAll()
//                     .anyRequest().authenticated()
//             )
//             .formLogin(formLogin ->
//                 formLogin
//                     .loginPage("/x/login")
//                     .successHandler(customSuccessHandler())
//                     .failureHandler(customFailureHandler())
//                     .permitAll()
//             )
//             .logout(logout ->
//                 logout
//                     .logoutSuccessUrl("/x/login")
//             );

//         return http.build();
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public AuthenticationSuccessHandler customSuccessHandler() {
//         return (request, response, authentication) -> {
//             response.sendRedirect("http://localhost:4200");
//         };
//     }

//     @Bean
//     public AuthenticationFailureHandler customFailureHandler() {
//         return (request, response, exception) -> {
//             response.sendRedirect("/x/login?error");
//         };
//     }
// }
