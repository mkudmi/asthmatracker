package com.example.asthmatracker.controller;

import com.example.asthmatracker.models.Medicine;
import com.example.asthmatracker.models.TakingMedication;
import com.example.asthmatracker.models.TakingMedicationView;
import com.example.asthmatracker.service.MedicineService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/medicine")
public class MedicineController {

    private final MedicineService service;

    public MedicineController(MedicineService service) {
        this.service = service;
    }

    @PostMapping("/create-medicine")
    @ResponseStatus(HttpStatus.CREATED)
    public Medicine create(@Valid @RequestBody Medicine medicine) {
        return service.create(medicine);
    }

    @GetMapping("/by-name")
    public List<Medicine> findByName(@RequestParam(required = false) String name) {
        return service.findByName(name);
    }

    @GetMapping("/by-patient")
    public List<Medicine> findByPatient(
            @RequestParam(name = "patient_id") @Positive Integer patientId
    ) {
        return service.findByPatient(patientId);
    }

    @PostMapping("/taking-medication")
    @ResponseStatus(HttpStatus.CREATED)
    public TakingMedication recordTaking(@Valid @RequestBody TakingMedication takingMedication) {
        return service.recordTaking(takingMedication);
    }

    @GetMapping("/taking-medicine")
    public List<TakingMedicationView> findTaken(
            @RequestParam(required = false) @Pattern(regexp = "\\d{16}") String oms,
            @RequestParam(name = "patient_id", required = false) @Positive Integer patientId,
            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return service.findTaken(oms, patientId, startDate, endDate);
    }
}
