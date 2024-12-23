package gymtime.gymtime_core.auth.handler;

import gymtime.gymtime_core.auth.oauth2.dto.CustomOAuth2User;
import gymtime.gymtime_core.auth.user.User;
import gymtime.gymtime_core.auth.user.UserType;
import gymtime.gymtime_core.auth.user.dto.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            UserType userType = customUserDetails.getUserType();
            log.info("CustomUserDetails 객체: {}", customUserDetails);
            log.info("유저타입 확인: {}", userType);
            if (userType == UserType.BUSINESS) {
                response.sendRedirect("http://localhost:8080/business");
            } else if (userType == UserType.CUSTOMER) {
                response.sendRedirect("http://localhost:8080/customer");
            } else {
                response.sendRedirect("/");
            }
        } else if (principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            UserType userType = customOAuth2User.getUserType();

            if (userType == UserType.BUSINESS) {
                response.sendRedirect("http://localhost:8080/business");
            } else if (userType == UserType.CUSTOMER) {
                response.sendRedirect("http://localhost:8080/customer");
            } else {
                response.sendRedirect("/");
            }
        }


    }
}
