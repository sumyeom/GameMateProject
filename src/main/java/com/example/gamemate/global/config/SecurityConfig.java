package com.example.gamemate.global.config;

import com.example.gamemate.global.config.auth.*;
import com.example.gamemate.global.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *스프링 시큐리티의 인가 및 설정을 담당
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final DelegatedAuthenticationEntryPoint authenticationEntryPoint;
    private final DelegatedAccessDeniedHandler accessDeniedHandler;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable()) //CSRF 보호 비활성화 (REST API이므로)
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-resources/**" ,"/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/auth/signup", "/auth/login", "/auth/refresh", "/auth/email/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**", "/auth/oauth2/**").permitAll()
                        .requestMatchers("/oauth2-login.html", "/oauth2-login-failure.html", "/oauth2-login-success.html", "/oauth2-set-password.html").permitAll()
                        .requestMatchers(HttpMethod.POST,"/games/requests").hasRole("USER")
                        .requestMatchers("/games", "/games/{id}").hasRole("ADMIN")
                        .requestMatchers("/games/requests/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/oauth2/authorization")
                                .authorizationRequestRepository(authorizationRequestRepository()))
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler))
                .exceptionHandling(hanling-> hanling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // 여기서 CustomUserDetailsService가 주입됨
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_USER");
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

}