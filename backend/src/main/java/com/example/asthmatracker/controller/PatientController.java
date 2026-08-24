package com.example.asthmatracker.controller;

import com.example.asthmatracker.models.Patient;
import com.example.asthmatracker.models.PatientLoginRequest;
import com.example.asthmatracker.models.PatientRegistration;
import com.example.asthmatracker.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Создать пациента
     * */
    @PostMapping
    public Patient createPatient(@Valid @RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    /**
     * Получить список пациентов по фильтру
     * */
    @GetMapping
    public List<Patient> getPatientsByFilter(
            @RequestParam(required = false) String full_name,
            @RequestParam(required = false) String oms) {
        return patientService.getPatientsByFilter(full_name, oms);
    }

    /**
     * Изменить данные пациента
     * */
    @PutMapping("/{id}")
    public Patient updatePatient(
            @Valid @RequestBody Patient patient,
            @PathVariable Integer id) {
        return patientService.updatePatient(patient, id);
    }

    /**
     * Удалить пациента
     * */
    @DeleteMapping
    public void deletePatientByOms(@RequestParam String oms) {
        patientService.deletePatientByOms(oms);
    }

    /**
     * Присвоить пользователю пароль
     * */
    @PostMapping("/register")
    public PatientRegistration createPatientPassword(@RequestBody PatientRegistration patientRegistration) {
        return patientService.createPatientPassword(patientRegistration);
    }

    /**
     * Проверить, что пароль пользователя корректный
     * */
    @PostMapping("/validate")
    public boolean validateLogin(@RequestBody PatientLoginRequest request) {
        return patientService.isLoginValid(request.getOms(), request.getPassword());
    }
}
