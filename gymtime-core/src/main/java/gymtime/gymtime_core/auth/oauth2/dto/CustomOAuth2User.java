package gymtime.gymtime_core.auth.oauth2.dto;

import gymtime.gymtime_core.auth.user.User;
import gymtime.gymtime_core.auth.user.UserType;
import gymtime.gymtime_core.auth.user.dto.UserRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final UserRequest userRequest;

    public CustomOAuth2User(UserRequest userRequest) {
        this.userRequest = userRequest;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority("ROLE_" + userRequest.getUserType().name())); // UserType을 GrantedAuthority로 변환
        return collection;
    }

    @Override
    public String getName() {
        return userRequest.getUsername();
    }

    public String getLoginId() {
        return userRequest.getLoginId();
    }

    public UserType getUserType() {
        return userRequest.getUserType();
    }
}
