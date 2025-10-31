package com.jacob.backend.dto;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "A data inicial não pode ser nula")
    private LocalDate initDate;
    @NotNull(message = "A data final não pode ser nula")
    private LocalDate finishDate;
    @NotNull(message = "A data do pagamento incial não pode ser nula")
    private LocalDate initPayment;
    @NotNull(message = "O valor do empréstimo não pode ser nulo")
    private Double loan;
    @NotNull(message = "O valor do juros não pode ser nulo")
    private Double percent;

    public String validateDates() {
        if (this.finishDate.isBefore(this.initDate) &&
            this.finishDate.isEqual(this.initDate)){
            return "Verifique as datas! A data final deve ser maior que a data inicial.";
        }
        if (this.initPayment.isBefore(this.initDate) ||
                this.initPayment.isAfter(this.finishDate)) {
            return "Verifique as datas! O primeiro pagamento deve estar entre a data inicial e final.";
        }
        return "OK";
    }

}
