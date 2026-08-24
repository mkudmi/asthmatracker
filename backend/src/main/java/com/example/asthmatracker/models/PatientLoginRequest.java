package com.example.asthmatracker.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PatientLoginRequest(
        @NotBlank @Pattern(regexp = "\\d{16}") String oms,
        @NotBlank String password
) {
}
