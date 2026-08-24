package com.example.asthmatracker.controller;

import com.example.asthmatracker.models.Doctors;
import com.example.asthmatracker.models.DoctorsRegistration;
import com.example.asthmatracker.service.DoctorsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorsController {

    private final DoctorsService doctorsService;

    public DoctorsController(DoctorsService doctorsService) {
        this.doctorsService = doctorsService;
    }

    /**
     * Создать доктора
     */
    @PostMapping
    public Doctors createDoctor(@Valid @RequestBody Doctors doctors) {
        return doctorsService.createDoctor(doctors);
    }

    /**
     * Получить информацию о докторе
     */
    @GetMapping("/doctor")
    public Doctors getDoctorByPersonnelNumber(
            @RequestParam String personnel_number) {
        return doctorsService.getDoctorByPersonnelNumber(personnel_number);
    }

    /**
     * Проверить, что пароль доктора корректный
     */
    @GetMapping("/validate")
    public boolean validateLogin(
            @RequestParam String personnel_number,
            @RequestParam String password) {
        return doctorsService.isLoginValid(personnel_number, password);
    }

    /**
     * Присвоить доктору пароль
     */
    @PostMapping("/register")
    public DoctorsRegistration createDoctorsPassword(@RequestBody DoctorsRegistration doctorsRegistration) {
        return doctorsService.createDoctorPassword(doctorsRegistration);
    }
}
