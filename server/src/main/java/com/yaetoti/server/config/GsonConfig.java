package com.yaetoti.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import network.adapters.GsonBytesBase64Adapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class GsonConfig {
  @Bean
  @Primary
  public Gson Gson() {
    return new GsonBuilder()
      .registerTypeAdapter(byte[].class, new GsonBytesBase64Adapter())
      .create();
  }
}
