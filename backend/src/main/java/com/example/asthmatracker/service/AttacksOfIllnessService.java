package com.example.asthmatracker.service;

import com.example.asthmatracker.models.AttacksOfIllness;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.example.asthmatracker.persistence.DatabaseTables.ATTACKS;
import static com.example.asthmatracker.persistence.DatabaseTables.ATTACK_DATE_TIME;
import static com.example.asthmatracker.persistence.DatabaseTables.ATTACK_ID;
import static com.example.asthmatracker.persistence.DatabaseTables.ATTACK_PATIENT_ID;
import static com.example.asthmatracker.persistence.DatabaseTables.ATTACK_SCALE;

@Service
public class AttacksOfIllnessService {

    private final DSLContext dsl;

    public AttacksOfIllnessService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public AttacksOfIllness create(AttacksOfIllness attack) {
        Integer id = dsl.insertInto(ATTACKS)
                .set(ATTACK_PATIENT_ID, attack.patientId())
                .set(ATTACK_DATE_TIME, attack.dateTime())
                .set(ATTACK_SCALE, attack.scale())
                .returning(ATTACK_ID)
                .fetchOne(ATTACK_ID);

        return new AttacksOfIllness(id, attack.patientId(), attack.dateTime(), attack.scale());
    }

    public List<AttacksOfIllness> find(Integer patientId, LocalDate startDate, LocalDate endDate) {
        return dsl.select(ATTACK_ID, ATTACK_PATIENT_ID, ATTACK_DATE_TIME, ATTACK_SCALE)
                .from(ATTACKS)
                .where(ATTACK_PATIENT_ID.eq(patientId))
                .and(DateRange.condition(ATTACK_DATE_TIME, startDate, endDate))
                .orderBy(ATTACK_DATE_TIME.asc())
                .fetch(this::map);
    }

    private AttacksOfIllness map(Record record) {
        return new AttacksOfIllness(
                record.get(ATTACK_ID),
                record.get(ATTACK_PATIENT_ID),
                record.get(ATTACK_DATE_TIME),
                record.get(ATTACK_SCALE)
        );
    }
}
