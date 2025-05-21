package com.yaetoti.server.controllers;

import com.yaetoti.server.repositories.PrintRepository;
import com.yaetoti.server.entries.PrintEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/print")
public class PrintController {
  @Autowired
  private PrintRepository repository;

  @GetMapping("/{key}")
  public ResponseEntity<String> getValue(@PathVariable String key) {
    PrintEntry entry = repository.findByKey(key);
    if (entry == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(entry.GetValue(), HttpStatus.OK);
  }
}
