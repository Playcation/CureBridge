package com.example.memberservice.controller.doc;

import com.example.memberservice.dto.LoginRequestDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginDocsController {

  @PostMapping("/user/login")
  public void login(
      @RequestBody(description = "로그인 정보", required = true) LoginRequestDto loginRequestDto) {
    // 문서용이므로 실제 구현은 없음
  }

  @PostMapping("/user/logout")
  public void logout() {
    // 문서용이므로 실제 구현은 없음
  }
}
