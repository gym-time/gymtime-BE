package gymtime.gymtime_core.auth.oauth2.service;

import gymtime.gymtime_core.auth.oauth2.dto.CustomOAuth2User;
import gymtime.gymtime_core.auth.oauth2.dto.NaverResponse;
import gymtime.gymtime_core.auth.oauth2.dto.OAuth2Response;
import gymtime.gymtime_core.auth.repository.UserRepository;
import gymtime.gymtime_core.auth.user.User;
import gymtime.gymtime_core.auth.user.UserType;
import gymtime.gymtime_core.auth.user.dto.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.debug("Loading user: {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        //네이버 사용자 정보 처리
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        //네이버 로그인 ID 생성
        String loginId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        Optional<User> existData = userRepository.findByLoginId(loginId);

        if (existData.isEmpty()) {
            User user = new User();
            user.setLoginId(loginId);
            user.setEmail(oAuth2Response.getEmail());
            user.setUsername(oAuth2Response.getName());
            user.setUserType(UserType.CUSTOMER);

            userRepository.save(user);

            UserRequest registerRequest = new UserRequest();
            registerRequest.setLoginId(loginId);
            registerRequest.setLoginId(loginId);
            registerRequest.setEmail(oAuth2Response.getEmail());
            registerRequest.setUsername(oAuth2Response.getName());
            registerRequest.setUserType(UserType.CUSTOMER);

            return new CustomOAuth2User(registerRequest);
        } else {
            //기존 사용자 업데이트
            User existingUser = existData.get(); // Optional에서 값 추출
            existingUser.setEmail(oAuth2Response.getEmail());
            existingUser.setUsername(oAuth2Response.getName());

            userRepository.save(existingUser);

            UserRequest updateRequest = new UserRequest();
            updateRequest.setLoginId(existingUser.getLoginId());
            updateRequest.setEmail(existingUser.getEmail());
            updateRequest.setUsername(existingUser.getUsername());
            updateRequest.setUserType(existingUser.getUserType());

            return new CustomOAuth2User(updateRequest);
        }

        /*//첫번째 로그인임
        Optional<User> byLoginId = userRepository.findByLoginId(loginId);
        if (byLoginId.isEmpty()) {
            User newUser = new User();
            newUser.setLoginId(loginId);
            newUser.setEmail(oAuth2Response.getEmail());
            newUser.setUsername(oAuth2Response.getName());
            newUser.setUserType(UserType.CUSTOMER); // UserType 기본값 CUSTOMER
            userRepository.save(newUser);

            return new CustomUserDetails(newUser, true);

        } else {
            return new CustomUserDetails(byLoginId.get(), false);
        }*/
    }
}
