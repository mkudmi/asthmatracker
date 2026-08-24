package com.example.asthmatracker.controller;

import com.example.asthmatracker.models.DoctorLoginRequest;
import com.example.asthmatracker.models.Doctors;
import com.example.asthmatracker.models.DoctorsRegistration;
import com.example.asthmatracker.models.RegistrationResponse;
import com.example.asthmatracker.service.DoctorsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/doctors")
public class DoctorsController {

    private final DoctorsService service;

    public DoctorsController(DoctorsService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doctors create(@Valid @RequestBody Doctors doctor) {
        return service.create(doctor);
    }

    @GetMapping("/doctor")
    public Doctors getByPersonnelNumber(
            @RequestParam(name = "personnel_number") @NotBlank String personnelNumber
    ) {
        return service.getByPersonnelNumber(personnelNumber);
    }

    @PostMapping("/validate")
    public boolean validateLogin(@Valid @RequestBody DoctorLoginRequest request) {
        return service.isLoginValid(request.personnelNumber(), request.password());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse register(@Valid @RequestBody DoctorsRegistration request) {
        return service.register(request);
    }
}
