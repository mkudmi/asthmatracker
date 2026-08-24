package com.example.asthmatracker.controller;

import com.example.asthmatracker.models.Spirometry;
import com.example.asthmatracker.service.SpirometryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/spirometry")
public class SpirometryController {

    private final SpirometryService spirometryService;

    public SpirometryController(SpirometryService spirometryService) {
        this.spirometryService = spirometryService;
    }

    /**
     * Записать результат спирометрии
     * */
    @PostMapping
    public Spirometry postSpirometry(@RequestBody Spirometry spirometry) {
        return spirometryService.postResultOfSpirometry(spirometry);
    }

    /**
     * Получить список результатов спирометрии
     * */
    @GetMapping
    public List<Spirometry> getSpirometryByFilter(
            @RequestParam() Integer patient_id,
            @RequestParam(required = false) LocalDate start_date,
            @RequestParam(required = false) LocalDate end_date
    ) {
        return spirometryService.getSpirometryByFilter(patient_id, start_date, end_date);
    }
}
