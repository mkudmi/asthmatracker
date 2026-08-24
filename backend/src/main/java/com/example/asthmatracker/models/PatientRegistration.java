package com.example.asthmatracker.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientRegistration(
        @NotBlank @Pattern(regexp = "\\d{16}") String oms,
        @NotBlank @Size(min = 8, max = 72) String password
) {
}
