package com.example.asthmatracker.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientRegistration {
    private Integer id;
    private String oms;
    private String password;
}
