package com.jacob.backend.controller;

import com.jacob.backend.dto.InstallmentsDto;
import com.jacob.backend.dto.RequestParams;
import com.jacob.backend.service.CalculatorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @PostMapping("/calculate")
    public ResponseEntity<InstallmentsDto> calculate(@Valid @RequestBody RequestParams params) {
        String dateValid = params.validateDates();
        if (dateValid.equals("OK")) {
            return ResponseEntity.status(HttpStatus.OK).body(service.calculate(params));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, dateValid);
        }
    }
}
