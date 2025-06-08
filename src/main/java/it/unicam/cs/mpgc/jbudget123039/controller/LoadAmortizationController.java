package it.unicam.cs.mpgc.jbudget123039.controller;

import it.unicam.cs.mpgc.jbudget123039.persistence.entity.ScheduledMovementEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.entity.TagEntity;
import it.unicam.cs.mpgc.jbudget123039.persistence.repository.ScheduledMovementRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

public class LoadAmortizationController {

    private final ScheduledMovementRepository dao = new ScheduledMovementRepository();

    public CompletionStage<Void> generateSchedule(BigDecimal totalAmount, BigDecimal interestRate, int installments, LocalDate startDate, List<TagEntity> tags) {
        List<ScheduledMovementEntity> schedule = new ArrayList<>();
        BigDecimal principal = totalAmount.divide(BigDecimal.valueOf(installments), 2, RoundingMode.HALF_UP);

        for (int i = 0; i < installments; i++) {
            BigDecimal interest = totalAmount.multiply(interestRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = principal.add(interest);

            ScheduledMovementEntity entity = new ScheduledMovementEntity();
            entity.setDescription("Rata prestito #" + (i + 1));
            entity.setAmount(total.negate());
            entity.setIncome(false);
            entity.setScheduledDate(startDate.plusMonths(i));
            entity.setRecurrenceDays(0);
            entity.setTags(tags);

            schedule.add(entity);
        }

        List<CompletionStage<Void>> futures = schedule.stream()
                .map(dao::saveScheduledMovementAsync)
                .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
}