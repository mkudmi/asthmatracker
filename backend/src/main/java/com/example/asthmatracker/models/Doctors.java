package com.example.asthmatracker.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Doctors(
        Integer id,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String surname,
        @NotBlank @Size(max = 50) String personnelNumber
) {
}
