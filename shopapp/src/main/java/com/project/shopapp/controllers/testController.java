package com.project.shopapp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v2/test")
@RequiredArgsConstructor
public class testController {

    //get order with user Id
    @GetMapping("")
    public ResponseEntity<?> getOrders() {

        return ResponseEntity.ok("hello test");

    }
}
