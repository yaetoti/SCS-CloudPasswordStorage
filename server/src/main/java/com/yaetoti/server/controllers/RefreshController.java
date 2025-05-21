package com.yaetoti.server.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yaetoti.server.config.KeyProvider;
import com.yaetoti.server.entries.UsersEntry;
import com.yaetoti.server.repositories.UsersRepository;
import network.payload.AccessTokenData;
import network.payload.RefreshTokenData;
import network.payload.Token;
import network.responses.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/refresh")
public class RefreshController {
  private final Gson m_gson;
  private final UsersRepository m_users;
  private final KeyProvider m_keyProvider;

  RefreshController(Gson gson, UsersRepository users, KeyProvider keyProvider) {
    m_gson = gson;
    m_users = users;
    m_keyProvider = keyProvider;
  }

  @PostMapping
  public ResponseEntity<String> Post(@RequestBody String rawJson) {
    try {
      Token token = m_gson.fromJson(rawJson, Token.class);
      if (!token.IsValid(m_keyProvider.GetPublicKey())) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      RefreshTokenData tokenData = m_gson.fromJson(token.GetJsonData(), RefreshTokenData.class);
      if (Instant.now().getEpochSecond() > tokenData.expiresAt) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      UsersEntry entry = m_users.findById(tokenData.userId).orElse(null);
      if (entry == null) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      AccessTokenData accessTokenData = new AccessTokenData();
      accessTokenData.userId = entry.id;
      accessTokenData.isAdmin = entry.is_admin;
      accessTokenData.issuedAt = Instant.now().getEpochSecond();
      //accessTokenData.expiresAt = accessTokenData.issuedAt + 3600;
      accessTokenData.expiresAt = accessTokenData.issuedAt + 90;

      RefreshTokenData refreshTokenData = new RefreshTokenData();
      refreshTokenData.userId = entry.id;
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
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }
}
