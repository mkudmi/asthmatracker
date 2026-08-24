package com.example.asthmatracker.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttacksOfIllness {
    private Integer id;
    private Integer patient_id;
    private LocalDateTime date_time;
    private Integer scale;
}
