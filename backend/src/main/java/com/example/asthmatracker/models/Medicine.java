package com.example.asthmatracker.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record Medicine(
        Integer id,
        @NotBlank @Size(max = 150) String name,
        @NotNull @Positive Integer mkg
) {
}
