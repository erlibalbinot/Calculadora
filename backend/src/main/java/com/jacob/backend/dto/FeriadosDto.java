package com.jacob.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class FeriadosDto {
    private LocalDate date;
    //private String name;
    //private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeriadosDto feriado = (FeriadosDto) o;
        return date.equals(feriado.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
