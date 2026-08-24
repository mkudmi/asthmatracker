package com.example.asthmatracker.service;

import com.example.asthmatracker.web.BadRequestException;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.time.LocalDateTime;

final class DateRange {

    private DateRange() {
    }

    static Condition condition(Field<LocalDateTime> field, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("start_date must not be after end_date");
        }

        Condition condition = DSL.noCondition();
        if (startDate != null) {
            condition = condition.and(field.ge(startDate.atStartOfDay()));
        }
        if (endDate != null) {
            condition = condition.and(field.lt(endDate.plusDays(1).atStartOfDay()));
        }
        return condition;
    }
}
