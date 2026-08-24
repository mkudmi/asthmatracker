package com.example.asthmatracker.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientLoginRequest {
    private String oms;
    private String password;
}
