package com.yaetoti.server.entries;

import jakarta.persistence.*;

@Entity
@Table(name = "passwords")
public class PasswordsEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Long userId;
  public byte[] encrypted_password;
  @Column(name = "cipherId")
  public Long cipherId;
  public String name;
  @Column(name = "keyHash")
  public byte[] keyHash;
}
