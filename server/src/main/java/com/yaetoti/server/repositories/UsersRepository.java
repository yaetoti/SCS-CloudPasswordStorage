package com.yaetoti.server.repositories;

import com.yaetoti.server.entries.UsersEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersEntry, Long> {
  Optional<UsersEntry> findByUsername(String username);
}
