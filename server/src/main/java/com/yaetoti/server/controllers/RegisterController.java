package com.yaetoti.server.controllers;

import com.google.gson.Gson;
import com.yaetoti.server.entries.UsersEntry;
import com.yaetoti.server.repositories.UsersRepository;
import network.requests.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegisterController {
  private final Gson m_gson;
  private final UsersRepository m_users;

  RegisterController(Gson gson, UsersRepository users) {
    m_gson = gson;
    m_users = users;
  }

  @PostMapping
  public ResponseEntity<String> Post(@RequestBody String rawJson) {
    try {
      // Decode
      RegisterRequest requestData = m_gson.fromJson(rawJson, RegisterRequest.class);
      // Check for existence
      if (m_users.findByUsername(requestData.username).isPresent()) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      // Create new
      UsersEntry entry = new UsersEntry();
      entry.username = requestData.username;
      entry.key_hash = requestData.keyHash;
      m_users.save(entry);

      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}
