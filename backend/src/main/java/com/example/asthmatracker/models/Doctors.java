package com.example.asthmatracker.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Doctors {
    private Integer id;
    private String name;
    private String surname;
    private String personnel_number;
}
