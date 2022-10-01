package de.qr.calendar.repository;

import de.qr.calendar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);

    List<User> findAllByUsernameStartingWith(String usernameStart);

}
