package com.example.asthmatracker.models;

import jakarta.validation.constraints.NotBlank;

public record DoctorLoginRequest(
        @NotBlank String personnelNumber,
        @NotBlank String password
) {
}
