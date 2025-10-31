package com.jacob.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestParams {

    private LocalDate initDate;
    private LocalDate finishDate;
    private LocalDate initPayment;
    private Double loan;
    private Double percent;

}
