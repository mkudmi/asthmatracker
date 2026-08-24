package com.example.asthmatracker.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TakingMedicationView {
    private Integer patient_id;
    private String oms;
    private Integer medicine_id;
    private String medicine_name;
    private Integer mkg;
    private LocalDateTime date_time;
}

