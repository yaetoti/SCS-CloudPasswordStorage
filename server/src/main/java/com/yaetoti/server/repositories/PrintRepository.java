package com.yaetoti.server.repositories;

import com.yaetoti.server.entries.PrintEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrintRepository extends JpaRepository<PrintEntry, Long> {
  PrintEntry findByKey(String key);
}
