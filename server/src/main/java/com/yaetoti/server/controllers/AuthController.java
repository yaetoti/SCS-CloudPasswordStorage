package com.yaetoti.server.controllers;

import com.google.gson.Gson;
import com.yaetoti.server.config.KeyProvider;
import com.yaetoti.server.entries.UsersEntry;
import com.yaetoti.server.repositories.UsersRepository;
import network.payload.AccessTokenData;
import network.payload.RefreshTokenData;
import network.payload.Token;
import network.requests.AuthRequest;
import network.responses.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final Gson m_gson;
  private final UsersRepository m_users;
  private final KeyProvider m_keyProvider;

  public AuthController(Gson gson, UsersRepository users, KeyProvider keyProvider) {
    m_gson = gson;
    m_users = users;
    m_keyProvider = keyProvider;
  }

  @PostMapping
  public ResponseEntity<String> Post(@RequestBody String rawJson) {
    AuthRequest requestData = m_gson.fromJson(rawJson, AuthRequest.class);
    Optional<UsersEntry> entry = m_users.findByUsername(requestData.username);
    if (entry.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (!Arrays.equals(entry.get().key_hash, requestData.keyHash)) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    AccessTokenData accessTokenData = new AccessTokenData();
    accessTokenData.userId = entry.get().id;
    accessTokenData.isAdmin = entry.get().is_admin;
    accessTokenData.issuedAt = Instant.now().getEpochSecond();
    //accessTokenData.expiresAt = accessTokenData.issuedAt + 3600;
    accessTokenData.expiresAt = accessTokenData.issuedAt + 90;

    RefreshTokenData refreshTokenData = new RefreshTokenData();
    refreshTokenData.userId = entry.get().id;
    refreshTokenData.issuedAt = Instant.now().getEpochSecond();
    //refreshTokenData.expiresAt = refreshTokenData.issuedAt + 3600 * 24 * 30;
    refreshTokenData.expiresAt = refreshTokenData.issuedAt + 120;

    AuthResponse response = new AuthResponse();
    response.accessToken = Token.Create(m_gson.toJson(accessTokenData), m_keyProvider.GetPrivateKey());
    response.refreshToken = Token.Create(m_gson.toJson(refreshTokenData), m_keyProvider.GetPrivateKey());

    return ResponseEntity
      .status(HttpStatus.OK)
      .header("Content-Type", "application/json")
      .body(m_gson.toJson(response));
  }
}
