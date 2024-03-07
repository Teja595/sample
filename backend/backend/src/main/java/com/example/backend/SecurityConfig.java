// package com.example.backend;

// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// @EnableWebSecurity
// public class SecurityConfig extends WebSecurityConfigurerAdapter {

//     @Override
//     protected void configure(HttpSecurity http) throws Exception {
//         http
//             .authorizeRequests()
//                 .antMatchers("/x/**").permitAll() // Allow access to /x endpoint
//                 .anyRequest().authenticated();
            
//             // Further configuration like formLogin, logout etc.
//     }
// }
