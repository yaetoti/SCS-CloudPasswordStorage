package com.yaetoti.server.entries;

import jakarta.persistence.*;

@Entity
@Table(name = "print")
public class PrintEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "key")
  private String key;
  @Column(name = "value")
  private String value;

  public String GetKey() {
    return key;
  }

  public String GetValue() {
    return value;
  }
}
