package pt.rumos.mymovies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.rumos.mymovies.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final String email);
}
