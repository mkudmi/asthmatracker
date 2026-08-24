package com.example.asthmatracker.controller;

import com.example.asthmatracker.models.Spirometry;
import com.example.asthmatracker.service.SpirometryService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/spirometry")
public class SpirometryController {

    private final SpirometryService service;

    public SpirometryController(SpirometryService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Spirometry create(@Valid @RequestBody Spirometry spirometry) {
        return service.create(spirometry);
    }

    @GetMapping
    public List<Spirometry> find(
            @RequestParam(name = "patient_id") @Positive Integer patientId,
            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return service.find(patientId, startDate, endDate);
    }
}
