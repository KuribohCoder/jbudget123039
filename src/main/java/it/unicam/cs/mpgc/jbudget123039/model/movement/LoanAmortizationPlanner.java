package it.unicam.cs.mpgc.jbudget123039.model.movement;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanAmortizationPlanner {

    public static List<ScheduledMovementEntity> generateSchedule(
            BigDecimal totalAmount,
            int numberOfInstallments,
            BigDecimal interestRate,
            LocalDate startDate,
            List<TagEntity> tags
    ) {
        List<ScheduledMovementEntity> installments = new ArrayList<>();

        BigDecimal principalPart = totalAmount.divide(BigDecimal.valueOf(numberOfInstallments), RoundingMode.HALF_UP);
        for (int i = 0; i < numberOfInstallments; i++) {
            BigDecimal interest = totalAmount.multiply(interestRate);
            BigDecimal totalInstallment = principalPart.add(interest);

            ScheduledMovementEntity movement = new ScheduledMovementEntity();
            movement.setDescription("Rata prestito #" + (i + 1));
            movement.setAmount(totalInstallment.negate());
            movement.setIncome(false);
            movement.setScheduledDate(startDate.plusMonths(i));
            movement.setTags(tags);

            installments.add(movement);
        }
        return installments;
    }
}