package io.cptn.common.repositories;

import io.cptn.common.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findFirstByEmail(String email);
}