package com.example.gateway.config;

import javax.crypto.SecretKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
public class JwtReactiveConfig {

  @Bean
  public ReactiveJwtDecoder reactiveJwtDecoder(SecretKey jwtSecretKey) {
    return NimbusReactiveJwtDecoder
        .withSecretKey(jwtSecretKey)
        .macAlgorithm(MacAlgorithm.HS384)
        .build();
  }

}

