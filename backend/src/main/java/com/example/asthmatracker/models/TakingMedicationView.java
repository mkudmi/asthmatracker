package com.example.asthmatracker.models;

import java.time.LocalDateTime;

public record TakingMedicationView(
        Integer patientId,
        String oms,
        Integer medicineId,
        String medicineName,
        Integer mkg,
        LocalDateTime dateTime
) {
}
