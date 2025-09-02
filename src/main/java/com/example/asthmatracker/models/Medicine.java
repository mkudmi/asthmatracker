package com.example.asthmatracker.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Medicine {
    private Integer id;
    private String name;
    private String mkg;
}
