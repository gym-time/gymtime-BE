package gymtime.gymtime_core.auth.jwt;

import gymtime.gymtime_core.auth.user.User;
import gymtime.gymtime_core.auth.user.UserType;
import gymtime.gymtime_core.auth.user.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        // 토큰 유효성 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            logger.info("Authorization header is missing or invalid");
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 이후 토큰만 추출
        String token = authorization.substring(7);

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            logger.warn("Token is expired");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authenticateUser(token);
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token) {
        String loginId = jwtUtil.getUsername(token);
        UserType userType = UserType.valueOf(jwtUtil.getUserType(token));

        //userEntity를 생성하여 값 set
        User user = new User();
        user.setLoginId(loginId);
        user.setPassword("hkimwjeongahwann");
        user.setUserType(userType);

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
