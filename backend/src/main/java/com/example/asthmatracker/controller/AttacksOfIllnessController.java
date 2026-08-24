package com.example.asthmatracker.controller;

import com.example.asthmatracker.models.AttacksOfIllness;
import com.example.asthmatracker.service.AttacksOfIllnessService;
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
@RequestMapping("/api/attacks")
public class AttacksOfIllnessController {

    private final AttacksOfIllnessService service;

    public AttacksOfIllnessController(AttacksOfIllnessService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttacksOfIllness create(@Valid @RequestBody AttacksOfIllness attack) {
        return service.create(attack);
    }

    @GetMapping
    public List<AttacksOfIllness> find(
            @RequestParam(name = "patient_id") @Positive Integer patientId,
            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return service.find(patientId, startDate, endDate);
    }
}
