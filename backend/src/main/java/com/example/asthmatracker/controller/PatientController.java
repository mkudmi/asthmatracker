package com.example.asthmatracker.controller;

import com.example.asthmatracker.models.Patient;
import com.example.asthmatracker.models.PatientLoginRequest;
import com.example.asthmatracker.models.PatientRegistration;
import com.example.asthmatracker.models.RegistrationResponse;
import com.example.asthmatracker.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@Valid @RequestBody Patient patient) {
        return service.create(patient);
    }

    @GetMapping
    public List<Patient> find(
            @RequestParam(name = "full_name", required = false) String fullName,
            @RequestParam(required = false) @Pattern(regexp = "\\d{16}") String oms
    ) {
        return service.find(fullName, oms);
    }

    @PutMapping("/{id}")
    public Patient update(
            @PathVariable @Positive Integer id,
            @Valid @RequestBody Patient patient
    ) {
        return service.update(id, patient);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestParam @Pattern(regexp = "\\d{16}") String oms
    ) {
        service.deleteByOms(oms);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse register(@Valid @RequestBody PatientRegistration request) {
        return service.register(request);
    }

    @PostMapping("/validate")
    public boolean validateLogin(@Valid @RequestBody PatientLoginRequest request) {
        return service.isLoginValid(request.oms(), request.password());
    }
}
