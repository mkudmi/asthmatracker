package com.example.asthmatracker.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record Spirometry(
        Integer id,
        @NotNull @Positive Integer patientId,
        @NotNull @Min(50) @Max(950) Integer result,
        @NotNull LocalDateTime dateTime
) {
}
