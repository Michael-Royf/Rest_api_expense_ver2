package com.michael.expense.security;

import com.michael.expense.security.filter.JWtAuthorizationFilter;
import com.michael.expense.security.filter.JwtAccessDeniedHandler;
import com.michael.expense.security.filter.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.michael.expense.constant.SecurityConstant.PUBLIC_URLS;
import static com.michael.expense.constant.SecurityConstant.PUBLIC_URLS_SWAGGER;

@EnableWebSecurity
public class WebSecurityConfig {

    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private JWtAuthorizationFilter jWtAuthorizationFilter;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    @Autowired
    public WebSecurityConfig(JwtAccessDeniedHandler jwtAccessDeniedHandler,
                             JWtAuthorizationFilter jWtAuthorizationFilter,
                             JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jWtAuthorizationFilter = jWtAuthorizationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }


    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(PUBLIC_URLS).permitAll()
                .antMatchers(PUBLIC_URLS_SWAGGER).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jWtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
