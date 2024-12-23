package gymtime.gymtime_core.config;

import gymtime.gymtime_core.auth.handler.CustomAuthenticationSuccessHandler;
import gymtime.gymtime_core.auth.jwt.JwtFilter;
import gymtime.gymtime_core.auth.jwt.JwtUtil;
import gymtime.gymtime_core.auth.jwt.LoginFilter;
import gymtime.gymtime_core.auth.oauth2.handler.CustomSuccessHandler;
import gymtime.gymtime_core.auth.oauth2.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;


    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil, CustomOAuth2UserService oAuth2UserService, CustomSuccessHandler customSuccessHandler) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.oAuth2UserService = oAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //프론트 위해 3000 허용
        configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 메서드 허용
        configuration.setAllowCredentials(true); //true로 설정해야 프론트엔드에서 인증 정보를 포함한 요청이 가능
        configuration.setAllowedHeaders(Collections.singletonList("*")); //클라이언트 요청에서 모든 헤더를 허용
        configuration.setExposedHeaders(Collections.singletonList("Authorization")); //클라이언트가 응답 헤더 중 "Authorization" 헤더를 읽을 수 있도록 노출
        configuration.setMaxAge(3600L); //캐시 유효 기간 3600(1시간)
        return request -> configuration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("/api/login");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/api/user/login", "/api/user/join").permitAll()
                        .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/business/**").hasRole("BUSINESS")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated())
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler))
                .logout(logout -> logout
                        .logoutUrl("logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/login"))
                        .deleteCookies("JSESSIONID"))
                .addFilterAfter(new JwtFilter(jwtUtil), LoginFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
