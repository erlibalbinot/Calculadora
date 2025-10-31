package com.jacob.backend.service;

import com.jacob.backend.apiRequests.FeriadosNacionais;
import com.jacob.backend.dto.FeriadosDto;
import com.jacob.backend.dto.InstallmentDto;
import com.jacob.backend.dto.InstallmentsDto;
import com.jacob.backend.dto.RequestParams;
import com.jacob.backend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CalculatorService {

    @Autowired
    private FeriadosNacionais feriadosNacionais;

    public InstallmentsDto calculate(RequestParams params) {

        InstallmentDto installment = new InstallmentDto(params.getInitDate(), params.getLoan(), params.getLoan(), null, 0.0, 0.0, params.getLoan(), 0.0, 0.0, 0.0);

        InstallmentsDto installments = new InstallmentsDto(new ArrayList<>(), getInstallment(params));

        if (!installment.getDate().isEqual(params.getInitPayment()))
            installments.getInstallmentsData().add(installment);
        else
            installment.setDate(params.getInitDate().minusDays(1));

        long installmentsQtde = installments.getInstallmentsQtde();
        Integer installmentsExecution = 1;
        Integer paymentDay = params.getInitPayment().getDayOfMonth();
        List<FeriadosDto> feriados = Collections.emptyList();

        do {
            if (feriados.isEmpty() || feriados.get(0).getDate().getYear() != installment.getDate().getYear()) {
                feriados = feriadosNacionais.getFeriados(installment.getDate().getYear());
            }

            installment = calculateLineDay(installment.getDate(), installment.getBalance(), installment.getAccumulated(), params, installmentsExecution, paymentDay, installmentsQtde, feriados);
            if (installment.getConsolidated() != null) {
                installmentsExecution = ++installmentsExecution;
                if (installments.getInstallmentsData().isEmpty())
                    installment.setLoan(params.getLoan());
            }
            installments.getInstallmentsData().add(installment);
        } while (installmentsExecution <= installmentsQtde);

        return installments;
    }

    private InstallmentDto calculateLineDay(LocalDate lastDate, double lastBalance, double lastAccumulated, RequestParams params,
                                            Integer instQtde, int paymentDay, long installmentsQtde, List<FeriadosDto> feriados) {
        InstallmentDto installment = new InstallmentDto();

        LocalDate actualDate = calculateDateAndConsolidated(lastDate, params, instQtde, paymentDay, installment);

        if (installment.getConsolidated() != null) {
            actualDate = validateHolidayWeekend(actualDate, feriados);
        }

        if (actualDate.isAfter(params.getFinishDate())) {
            actualDate = params.getFinishDate();
            installment.setConsolidated(instQtde);
        }

        installment.setDate(actualDate);

        calculateValues(lastDate, lastBalance, lastAccumulated, params, installmentsQtde, installment);

        return installment;
    }

    private void calculateValues(LocalDate lastDate, double lastBalance, double lastAccumulated, RequestParams params,
                                 long installmentsQtde, InstallmentDto installment) {
        //amortizaçao
        installment.setAmortization(installment.getConsolidated() != null ? Math.max(0.00, params.getLoan() / installmentsQtde) : 0.00);
        //saldo
        installment.setBalance(Math.max(0.00, lastBalance - installment.getAmortization()));
        //provisao
        installment.setProvision(calculateProvision(lastDate, installment.getDate(), params.getPercent(), lastBalance + lastAccumulated));
        //pago
        installment.setPaid(installment.getConsolidated() != null ? Math.max(0.00, lastAccumulated + installment.getProvision()) : 0.00);
        //acumulado
        installment.setAccumulated(Math.max(0.00, lastAccumulated + installment.getProvision() - installment.getPaid()));
        //saldo devedor
        installment.setAmountOwed(Math.max(0.00, installment.getBalance() + installment.getAccumulated()));
        //total
        installment.setInstallmentValue(Math.max(0.00, installment.getAmortization() + installment.getPaid()));
    }

    private LocalDate calculateDateAndConsolidated(LocalDate lastDate, RequestParams params, Integer instQtde, int paymentDay, InstallmentDto installment) {
        LocalDate actualDate;
        if (instQtde == 1) {
            if (isFirstPayDay(lastDate, params.getInitPayment())) {
                actualDate = params.getInitPayment();
                installment.setConsolidated(instQtde);
            } else if (lastDate.getDayOfMonth() < lastDate.getMonth().length(lastDate.isLeapYear())) {
                actualDate = lastDate.withDayOfMonth(lastDate.getMonth().length(lastDate.isLeapYear()));
            } else {
                actualDate = lastDate.plusMonths(1).withDayOfMonth(lastDate.plusMonths(1).getMonth().length(lastDate.isLeapYear()));
            }
        } else {
            if (lastDate.getDayOfMonth() != lastDate.getMonth().length(lastDate.isLeapYear())) {
                actualDate = lastDate.withDayOfMonth(lastDate.getMonth().length(lastDate.isLeapYear()));
                if (actualDate.getDayOfMonth() <= paymentDay) {
                    installment.setConsolidated(instQtde);
                }
            } else {
                actualDate = lastDate.plusMonths(1);
                if (actualDate.getDayOfMonth() > paymentDay ||
                        actualDate.getMonth().length(lastDate.isLeapYear()) >= paymentDay)
                    actualDate = actualDate.withDayOfMonth(paymentDay);

                installment.setConsolidated(instQtde);
            }
        }
        return actualDate;
    }

    private double calculateProvision(LocalDate lastDate, LocalDate date, double percent, double balanceAccumulated) {
        double a = (Math.pow(((percent / 100) + 1), (Utils.getDays(lastDate, date) / Utils.baseDays))) - 1;
        return a * balanceAccumulated;
    }

    private Boolean isFirstPayDay(LocalDate lastDate, LocalDate initPyament) {
        if (!lastDate.isBefore(initPyament))
            return false;

        int lasDayMonth = lastDate.getMonth().length(lastDate.isLeapYear());

        if (lastDate.getDayOfMonth() == lasDayMonth)
            lastDate = lastDate.plusMonths(1);

        return (lastDate.withDayOfMonth(lastDate.getMonth().length(lastDate.isLeapYear()))
                .isAfter(initPyament) || lastDate.withDayOfMonth(lastDate.getMonth().length(lastDate.isLeapYear()))
                .isEqual(initPyament));
    }

    private Boolean isFeriado(List<FeriadosDto> feriados, LocalDate date) {
        return feriados.contains(new FeriadosDto(date));
    }

    private long getInstallment(RequestParams params) {
        long install = Utils.getMonths(params.getInitDate(), params.getFinishDate());
        if (install == 1 && params.getInitPayment().isBefore(params.getFinishDate())) {
            install = 2;
        }
        return install;
    }

    private LocalDate validateHolidayWeekend(LocalDate actualDate, List<FeriadosDto> feriados) {
        actualDate = isFeriado(feriados, actualDate) ? actualDate.plusDays(1) : actualDate;

        actualDate = actualDate.getDayOfWeek().name().equals("SATURDAY") ? actualDate.plusDays(2) :
                actualDate.getDayOfWeek().name().equals("SUNDAY") ? actualDate.plusDays(1) : actualDate;

        return actualDate;
    }
}
