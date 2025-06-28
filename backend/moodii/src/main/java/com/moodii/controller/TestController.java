package com.moodii.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/me")
    public ResponseEntity<?> testJwt(Authentication auth) {
        System.out.println("Auth:" + auth);
        return ResponseEntity.ok(Map.of("user", auth.getPrincipal()));
    }
}
