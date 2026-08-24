package com.example.asthmatracker.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorsRegistration {
    private Integer id;
    private String personnel_number;
    private String password;
}
