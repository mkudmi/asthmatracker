package com.example.asthmatracker.models;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Patient {
    private Integer id;
    private String name;
    private String surname;
    private String patronymic;
    private LocalDate birthday;
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Некорректный формат email. Должен содержать символ '@' и домен с точкой."
    )
    private String email;
    @Pattern(
            regexp = "\\+7\\d{10}",
            message = "Номер телефона должен начинаться с +7 и содержать ровно 10 цифр после +7"
    )
    private String phone_number;
    @Pattern(
            regexp = "\\d{16}",
            message = "ОМС должен содержать ровно 16 цифр."
    )
    private String oms;
    private String sex;
    private Integer height;
}
