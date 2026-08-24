package com.example.asthmatracker.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TakingMedication(
        Integer id,
        @NotNull @Positive Integer patientId,
        @NotNull @Positive Integer medicineId,
        @NotNull LocalDateTime dateTime
) {
}
