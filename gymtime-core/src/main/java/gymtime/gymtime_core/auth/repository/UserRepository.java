package gymtime.gymtime_core.auth.repository;

import gymtime.gymtime_core.auth.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByLoginId(String login);

    Optional<User> findByLoginId(String login);
}