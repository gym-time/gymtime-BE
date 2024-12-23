package gymtime.gymtime_core.auth.oauth2.handler;

import gymtime.gymtime_core.auth.jwt.JwtUtil;
import gymtime.gymtime_core.auth.oauth2.dto.CustomOAuth2User;
import gymtime.gymtime_core.auth.user.UserType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public CustomSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String loginId = customUserDetails.getLoginId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        UserType userType = UserType.fromAuthority(auth.getAuthority());

        String token = jwtUtil.createJwt(loginId, userType, 60*60*1000L);
        response.addHeader("Authorization", "Bearer " + token);

        log.info("토큰 값: {}", "Bearer " + token);

        response.addCookie(createCookie("Authorization", token));
        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*1000);
        //cookie.setSecure(true); //https에서만 적용
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
