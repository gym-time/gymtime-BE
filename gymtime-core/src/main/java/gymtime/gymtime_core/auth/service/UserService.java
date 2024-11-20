package gymtime.gymtime_core.auth.service;

import gymtime.gymtime_core.auth.user.dto.UserRequest;
import gymtime.gymtime_core.auth.exception.auth.DuplicateLoginIdException;
import gymtime.gymtime_core.auth.repository.UserRepository;
import gymtime.gymtime_core.auth.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String EMPTY_FIELD_MESSAGE = "%s은(는) 필수 입력 사항입니다.";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(UserRequest.registerRequest registerRequest) {
        validateJoinDTO(registerRequest);
        validateDuplicateLoginId(registerRequest.getLoginId());
        User user = createUserFromDto(registerRequest);
        userRepository.save(user);
    }

    private void validateJoinDTO(UserRequest.registerRequest registerRequest) {
        if (registerRequest.getUserType() == null) {
            throw new IllegalArgumentException("유저 유형이 선택되지 않았습니다. 정확한 서비스를 제공하기 위해 '사업자' 또는 '일반 회원'을 선택해주세요.");
        }
        if (registerRequest.getLoginId() == null || registerRequest.getLoginId().isEmpty()) {
            throw new IllegalArgumentException(String.format(EMPTY_FIELD_MESSAGE, "아이디"));
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException(String.format(EMPTY_FIELD_MESSAGE, "비밀번호"));
        }
        if (registerRequest.getUsername() == null || registerRequest.getUsername().isEmpty()) {
            throw new IllegalArgumentException(String.format(EMPTY_FIELD_MESSAGE, "이름"));
        }
        if (registerRequest.getPhoneNum() == null || registerRequest.getPhoneNum().isEmpty()) {
            throw new IllegalArgumentException(String.format(EMPTY_FIELD_MESSAGE, "휴대폰 번호"));
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            throw new IllegalArgumentException(String.format(EMPTY_FIELD_MESSAGE, "이메일"));
        }
    }

    private void validateDuplicateLoginId(String loginId) {
        boolean isLoginIdDuplicated = userRepository.existsByLoginId(loginId);
        if (isLoginIdDuplicated) {
            throw new DuplicateLoginIdException("중복된 아이디 입니다.");
        }
    }

    private User createUserFromDto(UserRequest.registerRequest registerRequest) {
        User user = User.builder().
                loginId(registerRequest.getLoginId()).
                password(bCryptPasswordEncoder.encode(registerRequest.getPassword())).
                userType(registerRequest.getUserType()).
                username(registerRequest.getUsername()).
                email(registerRequest.getEmail()).
                phoneNum(registerRequest.getPhoneNum()).
                build();
        return user;
    }
}
