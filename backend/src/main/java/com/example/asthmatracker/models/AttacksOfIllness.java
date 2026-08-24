package com.example.asthmatracker.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AttacksOfIllness(
        Integer id,
        @NotNull @Positive Integer patientId,
        @NotNull LocalDateTime dateTime,
        @NotNull @Min(1) @Max(5) Integer scale
) {
}
