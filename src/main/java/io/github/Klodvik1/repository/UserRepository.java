package io.github.Klodvik1.repository;

import io.github.Klodvik1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
