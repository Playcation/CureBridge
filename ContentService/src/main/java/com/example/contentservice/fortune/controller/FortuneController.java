package com.example.contentservice.fortune.controller;

import com.example.contentservice.fortune.dto.FortuneResponseDto;
import com.example.contentservice.fortune.service.FortuneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/fortune")
@RequiredArgsConstructor
public class FortuneController {

    private final FortuneService fortuneService;

    @PostMapping
    public ResponseEntity<FortuneResponseDto> getFortune(
            @RequestParam("image") MultipartFile image,
            @RequestParam("birthDate") String birthDate) {

        String fortuneMessage = fortuneService.getFortune(image, birthDate);
        return ResponseEntity.ok(new FortuneResponseDto(fortuneMessage));
    }
}
