package gymtime.gymtime_core.auth.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 100)
    private String loginId;

    @Setter
    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = true)
    private String phoneNum;

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(nullable = false)
    private UserType userType;

    @Builder.Default
    private String imgUrl = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png";

    @Builder.Default
    private LocalDate createdAt = LocalDate.now();
}
