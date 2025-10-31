package com.jacob.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class InstallmentsDto {
    private List<InstallmentDto> installmentsData;
    private long installmentsQtde;
}
