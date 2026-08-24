package com.example.asthmatracker.service;

import com.example.asthmatracker.models.Medicine;
import com.example.asthmatracker.models.TakingMedication;
import com.example.asthmatracker.models.TakingMedicationView;
import com.example.asthmatracker.web.BadRequestException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.asthmatracker.persistence.DatabaseTables.*;

@Service
public class MedicineService {

    private final DSLContext dsl;

    public MedicineService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Medicine create(Medicine medicine) {
        Integer id = dsl.insertInto(MEDICINE)
                .set(MEDICINE_NAME, medicine.name())
                .set(MEDICINE_MKG, medicine.mkg())
                .returning(MEDICINE_ID)
                .fetchOne(MEDICINE_ID);
        return new Medicine(id, medicine.name(), medicine.mkg());
    }

    public List<Medicine> findByName(String name) {
        Condition condition = name == null || name.isBlank()
                ? DSL.noCondition()
                : DSL.lower(MEDICINE_NAME).like("%" + name.trim().toLowerCase() + "%");
        return dsl.select(MEDICINE_ID, MEDICINE_NAME, MEDICINE_MKG)
                .from(MEDICINE)
                .where(condition)
                .orderBy(MEDICINE_NAME, MEDICINE_MKG)
                .fetch(record -> new Medicine(
                        record.get(MEDICINE_ID),
                        record.get(MEDICINE_NAME),
                        record.get(MEDICINE_MKG)
                ));
    }

    public List<Medicine> findByPatient(Integer patientId) {
        Table<?> assignment = MEDICINE_TO_PATIENT.as("mp");
        Table<?> medicine = MEDICINE.as("m");
        Field<Integer> assignmentPatientId = integer("mp", "patient_id");
        Field<Integer> assignmentMedicineId = integer("mp", "medicine_id");
        Field<Integer> medicineId = integer("m", "id");
        Field<String> medicineName = varchar("m", "name");
        Field<Integer> medicineMkg = integer("m", "mkg");

        return dsl.select(medicineId, medicineName, medicineMkg)
                .from(assignment)
                .join(medicine).on(assignmentMedicineId.eq(medicineId))
                .where(assignmentPatientId.eq(patientId))
                .orderBy(medicineName, medicineMkg)
                .fetch(record -> new Medicine(
                        record.get(medicineId),
                        record.get(medicineName),
                        record.get(medicineMkg)
                ));
    }

    public TakingMedication recordTaking(TakingMedication taking) {
        Integer id = dsl.insertInto(TAKING_MEDICATION)
                .set(TAKING_PATIENT_ID, taking.patientId())
                .set(TAKING_MEDICINE_ID, taking.medicineId())
                .set(TAKING_DATE_TIME, taking.dateTime())
                .returning(TAKING_ID)
                .fetchOne(TAKING_ID);
        return new TakingMedication(id, taking.patientId(), taking.medicineId(), taking.dateTime());
    }

    public List<TakingMedicationView> findTaken(
            String oms,
            Integer patientId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if ((oms == null || oms.isBlank()) && patientId == null) {
            throw new BadRequestException("Either oms or patient_id is required");
        }

        Table<?> taking = TAKING_MEDICATION.as("tm");
        Table<?> patients = PATIENTS.as("p");
        Table<?> medicines = MEDICINE.as("m");
        Field<Integer> takingPatientId = integer("tm", "patient_id");
        Field<Integer> takingMedicineId = integer("tm", "medicine_id");
        Field<LocalDateTime> takingDateTime = timestamp("tm", "date_time");
        Field<Integer> patientTableId = integer("p", "id");
        Field<String> patientOms = varchar("p", "oms");
        Field<Integer> medicineId = integer("m", "id");
        Field<String> medicineName = varchar("m", "name");
        Field<Integer> medicineMkg = integer("m", "mkg");

        Condition identity = DSL.noCondition();
        if (oms != null && !oms.isBlank()) {
            identity = identity.and(patientOms.eq(oms.trim()));
        }
        if (patientId != null) {
            identity = identity.and(patientTableId.eq(patientId));
        }

        return dsl.select(takingPatientId, patientOms, takingMedicineId, medicineName, medicineMkg, takingDateTime)
                .from(taking)
                .join(patients).on(takingPatientId.eq(patientTableId))
                .join(medicines).on(takingMedicineId.eq(medicineId))
                .where(identity)
                .and(DateRange.condition(takingDateTime, startDate, endDate))
                .orderBy(takingDateTime.desc())
                .fetch(record -> new TakingMedicationView(
                        record.get(takingPatientId), record.get(patientOms), record.get(takingMedicineId),
                        record.get(medicineName), record.get(medicineMkg), record.get(takingDateTime)
                ));
    }

    private static Field<Integer> integer(String tableAlias, String column) {
        return DSL.field(DSL.name(tableAlias, column), Integer.class);
    }

    private static Field<String> varchar(String tableAlias, String column) {
        return DSL.field(DSL.name(tableAlias, column), String.class);
    }

    private static Field<LocalDateTime> timestamp(String tableAlias, String column) {
        return DSL.field(DSL.name(tableAlias, column), LocalDateTime.class);
    }
}
