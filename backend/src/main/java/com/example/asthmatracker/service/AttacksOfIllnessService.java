package com.example.asthmatracker.service;

import com.example.asthmatracker.models.AttacksOfIllness;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.example.jooq.generated.Tables.ATTACKS_OF_ILLNESS;

@Service
public class AttacksOfIllnessService {

    private final DSLContext dsl;

    public AttacksOfIllnessService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public AttacksOfIllness postAttack(AttacksOfIllness attacksOfIllness) {
        Record record = dsl.insertInto(ATTACKS_OF_ILLNESS)
                .set(ATTACKS_OF_ILLNESS.PATIENT_ID, attacksOfIllness.getPatient_id())
                .set(ATTACKS_OF_ILLNESS.DATE_TIME, attacksOfIllness.getDate_time())
                .set(ATTACKS_OF_ILLNESS.SCALE, attacksOfIllness.getScale())
                .returning(ATTACKS_OF_ILLNESS.ID)
                .fetchOne();

        if (record != null) {
            attacksOfIllness.setId(record.get(ATTACKS_OF_ILLNESS.ID));
        }

        return attacksOfIllness;
    }

    public List<AttacksOfIllness> getAttacksByFilter(Integer patientId, LocalDate startDate, LocalDate endDate) {
        return dsl.selectFrom(ATTACKS_OF_ILLNESS)
                .where(ATTACKS_OF_ILLNESS.PATIENT_ID.eq(patientId))
                .and(ATTACKS_OF_ILLNESS.DATE_TIME.between(
                        startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay().minusSeconds(1)
                ))
                .fetchInto(AttacksOfIllness.class);
    }
}
