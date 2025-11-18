package com.example.gateway.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtKeyConfig {

  //	@Value("${spring.jwt.secret}")
  private String base64Secret = "WEFWEGWGweffwetyqwfgwwgqegqafawegEFfewfweghsdfgaerg";

  @Bean
  public SecretKey jwtSecretKey() {
    byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
