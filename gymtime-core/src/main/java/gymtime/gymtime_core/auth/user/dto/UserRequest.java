package gymtime.gymtime_core.auth.user.dto;

import gymtime.gymtime_core.auth.user.UserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {

    private String loginId;

    private String password;

    private UserType userType;

    private String username;

    private String email;

    private String phoneNum;

    @Getter
    @Setter
    @Builder
    public static class registerRequest{
        private String loginId;

        private String password;

        private UserType userType;

        private String username;

        private String email;

        private String phoneNum;
    }
}
