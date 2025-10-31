package com.jacob.backend.controller;

import com.jacob.backend.dto.InstallmentsDto;
import com.jacob.backend.dto.RequestParams;
import com.jacob.backend.service.CalculatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @PostMapping("/calculate")
    public ResponseEntity<InstallmentsDto> calculate(@RequestBody RequestParams params) {
        return ResponseEntity.status(HttpStatus.OK).body(service.calculate(params));
    }
}
