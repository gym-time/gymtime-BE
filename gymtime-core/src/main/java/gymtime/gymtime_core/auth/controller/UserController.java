package gymtime.gymtime_core.auth.controller;

import gymtime.gymtime_core.auth.user.dto.UserRequest;
import gymtime.gymtime_core.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(@Validated @RequestBody UserRequest.registerRequest registerRequest) {
        userService.joinProcess(registerRequest);
        return ResponseEntity.ok("회원가입 성공");
    }

}
