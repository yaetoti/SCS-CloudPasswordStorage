package com.yaetoti.server.repositories;

import com.yaetoti.server.entries.PasswordsEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordsRepository extends JpaRepository<PasswordsEntry, Long> {
  PasswordsEntry findByName(String name);
  List<PasswordsEntry> findAllByUserId(Long userId);
}
