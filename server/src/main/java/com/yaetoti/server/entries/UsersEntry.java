package com.yaetoti.server.entries;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UsersEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String username;
  public byte[] key_hash;
  public boolean is_admin;
}
