package com.example.asthmatracker.service;

import com.example.asthmatracker.models.Spirometry;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.example.asthmatracker.persistence.DatabaseTables.SPIROMETRY;
import static com.example.asthmatracker.persistence.DatabaseTables.SPIROMETRY_DATE_TIME;
import static com.example.asthmatracker.persistence.DatabaseTables.SPIROMETRY_ID;
import static com.example.asthmatracker.persistence.DatabaseTables.SPIROMETRY_PATIENT_ID;
import static com.example.asthmatracker.persistence.DatabaseTables.SPIROMETRY_RESULT;

@Service
public class SpirometryService {

    private final DSLContext dsl;

    public SpirometryService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Spirometry create(Spirometry spirometry) {
        Integer id = dsl.insertInto(SPIROMETRY)
                .set(SPIROMETRY_PATIENT_ID, spirometry.patientId())
                .set(SPIROMETRY_RESULT, spirometry.result())
                .set(SPIROMETRY_DATE_TIME, spirometry.dateTime())
                .returning(SPIROMETRY_ID)
                .fetchOne(SPIROMETRY_ID);

        return new Spirometry(id, spirometry.patientId(), spirometry.result(), spirometry.dateTime());
    }

    public List<Spirometry> find(Integer patientId, LocalDate startDate, LocalDate endDate) {
        return dsl.select(SPIROMETRY_ID, SPIROMETRY_PATIENT_ID, SPIROMETRY_RESULT, SPIROMETRY_DATE_TIME)
                .from(SPIROMETRY)
                .where(SPIROMETRY_PATIENT_ID.eq(patientId))
                .and(DateRange.condition(SPIROMETRY_DATE_TIME, startDate, endDate))
                .orderBy(SPIROMETRY_DATE_TIME.asc())
                .fetch(this::map);
    }

    private Spirometry map(Record record) {
        return new Spirometry(
                record.get(SPIROMETRY_ID),
                record.get(SPIROMETRY_PATIENT_ID),
                record.get(SPIROMETRY_RESULT),
                record.get(SPIROMETRY_DATE_TIME)
        );
    }
}
