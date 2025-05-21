package com.yaetoti.server.config;

import com.yaetoti.server.ServerApplication;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import rsa.RSA;
import rsa.RsaKeyPair;
import rsa.RsaPrivateKey;
import rsa.RsaPublicKey;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class KeyProvider {
  private static final Path KEY_PATH = Paths.get("keys/server.key");
  private static final int KEY_SIZE = 1024;

  private RsaPrivateKey m_privateKey;
  private RsaPublicKey m_publicKey;

  @PostConstruct
  public void Init() {
    try {
      LoadKeys();
    } catch (RuntimeException e) {
      ServerApplication.LOGGER.warn("Failed to load keys, generating new ones", e);
      GenerateKeys();
      try {
        SaveKeys();
      } catch (RuntimeException e1) {
        ServerApplication.LOGGER.warn("Failed to save keys", e1);
      }
    }
  }

  public RsaPrivateKey GetPrivateKey() {
    return m_privateKey;
  }

  public RsaPublicKey GetPublicKey() {
    return m_publicKey;
  }

  private void GenerateKeys() {
    RsaKeyPair keys = RSA.GenerateKeys(KEY_SIZE);
    m_privateKey = keys.privateKey;
    m_publicKey = keys.publicKey;
  }

  private void LoadKeys() {
    if (!Files.exists(KEY_PATH)) {
      throw new RuntimeException("Keys not found");
    }

    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(KEY_PATH.toFile()));
      m_privateKey = (RsaPrivateKey) in.readObject();
      m_publicKey = (RsaPublicKey) in.readObject();
      in.close();
    } catch (IOException | ClassNotFoundException e) {
      ServerApplication.LOGGER.error("Failed to load keys", e);
      throw new RuntimeException(e);
    }
  }

  private void SaveKeys() {
    try {
      Files.createDirectories(KEY_PATH.getParent());
      Files.createFile(KEY_PATH);

      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(KEY_PATH.toFile()));
      out.writeObject(m_privateKey);
      out.writeObject(m_publicKey);
      out.close();
    } catch (IOException e) {
      ServerApplication.LOGGER.error("Failed to save keys", e);
      throw new RuntimeException(e);
    }
  }
}
