package com.yaetoti.server.controllers;

import com.google.gson.Gson;
import com.yaetoti.server.ServerApplication;
import com.yaetoti.server.config.KeyProvider;
import com.yaetoti.server.entries.PasswordsEntry;
import com.yaetoti.server.repositories.PasswordsRepository;
import network.payload.AccessTokenData;
import network.payload.PasswordData;
import network.payload.ServerPasswordData;
import network.payload.Token;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/password")
public class PasswordController {
  private final Gson m_gson;
  private final KeyProvider m_keyProvider;
  private final PasswordsRepository m_passwords;

  public PasswordController(Gson gson, KeyProvider keyProvider, PasswordsRepository passwords) {
    m_gson = gson;
    m_keyProvider = keyProvider;
    m_passwords = passwords;
  }

  @GetMapping
  public ResponseEntity<String> Get(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
    try {
      // Authorization
      if (auth.substring(0, 7).compareToIgnoreCase("Bearer ") != 0) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      Token token = m_gson.fromJson(auth.substring(7), Token.class);
      if (!token.IsValid(m_keyProvider.GetPublicKey())) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      AccessTokenData tokenData = m_gson.fromJson(token.GetJsonData(), AccessTokenData.class);
      if (Instant.now().getEpochSecond() > tokenData.expiresAt) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      // Logic
      List<PasswordsEntry> entries = m_passwords.findAllByUserId(tokenData.userId);
      ServerPasswordData[] passwords = new ServerPasswordData[entries.size()];
      int index = 0;
      for (var entry : entries) {
        var data = new ServerPasswordData();
        data.id = entry.id;
        data.name = entry.name;
        data.cipherId = entry.cipherId.intValue();
        data.encrypted_password = entry.encrypted_password;
        data.keyHash = entry.keyHash;

        passwords[index++] = data;
      }

      return ResponseEntity.status(HttpStatus.OK).body(m_gson.toJson(passwords));
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping
  public ResponseEntity<String> Post(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody String rawJson) {
    try {
      // Authorization
      if (!auth.startsWith("Bearer ")) {
        ServerApplication.LOGGER.error("Invalid authorization header: " + auth);
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      Token token = m_gson.fromJson(auth.substring(7), Token.class);
      if (!token.IsValid(m_keyProvider.GetPublicKey())) {
        ServerApplication.LOGGER.error("Invalid token: " + token.GetJsonData());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      AccessTokenData tokenData = m_gson.fromJson(token.GetJsonData(), AccessTokenData.class);
      if (Instant.now().getEpochSecond() > tokenData.expiresAt) {
        ServerApplication.LOGGER.error("Token expired: " + token.GetJsonData());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      // Logic
      try {
        // Save password
        PasswordData data = m_gson.fromJson(rawJson, PasswordData.class);
        PasswordsEntry entry = new PasswordsEntry();
        entry.name = data.name;
        entry.cipherId = Long.valueOf(data.cipherId);
        entry.encrypted_password = data.encrypted_password;
        entry.userId = tokenData.userId;
        entry.keyHash = data.keyHash;

        entry = m_passwords.save(entry);

        // Send response
        ServerPasswordData response = new ServerPasswordData();
        response.id = entry.id;
        response.name = entry.name;
        response.cipherId = entry.cipherId.intValue();
        response.encrypted_password = entry.encrypted_password;
        response.keyHash = entry.keyHash;

        return ResponseEntity.ok().body(m_gson.toJson(response));
      } catch (Exception e) {
        ServerApplication.LOGGER.error("Failed to save password: " + rawJson, e);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      ServerApplication.LOGGER.error("Exception: " + rawJson, e);
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> Delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @PathVariable String id) {
    try {
      // Authorization
      if (!auth.startsWith("Bearer ")) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      Token token = m_gson.fromJson(auth.substring(7), Token.class);
      if (!token.IsValid(m_keyProvider.GetPublicKey())) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      AccessTokenData tokenData = m_gson.fromJson(token.GetJsonData(), AccessTokenData.class);
      if (Instant.now().getEpochSecond() > tokenData.expiresAt) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      // Logic
      try {
        Long longId = Long.valueOf(id);
        ServerApplication.LOGGER.info("Deleting password with id: " + longId);

        PasswordsEntry entry = m_passwords.findById(Long.valueOf(id)).orElse(null);
        if (entry == null) {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (entry.userId != tokenData.userId) {
          return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        m_passwords.delete(entry);
        return new ResponseEntity<>(HttpStatus.OK);
      } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }
}
