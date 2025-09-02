package com.example.asthmatracker.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Medicine {
    private Integer id;
    private String name;
    private String mkg;
}
