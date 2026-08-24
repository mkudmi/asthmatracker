package com.example.asthmatracker.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record Patient(
        Integer id,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String surname,
        @Size(max = 100) String patronymic,
        @NotNull @Past LocalDate birthday,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Pattern(regexp = "\\+7\\d{10}") String phoneNumber,
        @NotBlank @Pattern(regexp = "\\d{16}") String oms,
        @NotBlank @Size(max = 20) String sex,
        @NotNull @Min(30) @Max(250) Integer height
) {
}
