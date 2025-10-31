package com.jacob.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class InstallmentDto {
    private LocalDate date;
    private double loan;
    private double amountOwed;
    private Integer consolidated;
    private double installmentValue;
    private double amortization;
    private double balance;
    private double provision;
    private double accumulated;
    private double paid;

    public InstallmentDto() {
        this.loan = 0.0;
        this.consolidated = null;
    }
}
