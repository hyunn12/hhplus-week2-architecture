package io.hhplus.arch.infrastructure;

import io.hhplus.arch.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
