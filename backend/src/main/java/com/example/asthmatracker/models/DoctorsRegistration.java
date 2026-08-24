package com.example.asthmatracker.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoctorsRegistration(
        @NotBlank @Size(max = 50) String personnelNumber,
        @NotBlank @Size(min = 8, max = 72) String password
) {
}
