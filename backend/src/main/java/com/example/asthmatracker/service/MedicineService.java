package com.example.asthmatracker.service;

import com.example.asthmatracker.models.Medicine;
import com.example.asthmatracker.models.TakingMedication;
import com.example.asthmatracker.models.TakingMedicationView;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.example.jooq.generated.Tables.*;

@Service
public class MedicineService {

    private final DSLContext dsl;

    public MedicineService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Medicine createMedicine(Medicine medicine) {
        Record record = dsl.insertInto(MEDICINE)
                .set(MEDICINE.NAME, medicine.getName())
                .set(MEDICINE.MKG, medicine.getMkg())
                .returning(MEDICINE.ID)
                .fetchOne();

        if (record != null) {
            medicine.setId(record.get(MEDICINE.ID));
        }

        return medicine;
    }

    public List<Medicine> getMedicineByName(String name) {
        return dsl.selectFrom(MEDICINE)
                .where(MEDICINE.NAME.eq(name))
                .fetchInto(Medicine.class);
    }

    public List<Medicine> getMedicineByPatient(Integer patientId) {
        return dsl.select(MEDICINE.ID, MEDICINE.NAME, MEDICINE.MKG).from(MEDICINE_TO_PATIENT)
                .join(MEDICINE).on(MEDICINE_TO_PATIENT.MEDICINE_ID.eq(MEDICINE.ID))
                .where(MEDICINE_TO_PATIENT.PATIENT_ID.eq(patientId))
                .fetchInto(Medicine.class);
    }

    public TakingMedication postTakingMedication(TakingMedication takingMedication) {
        Record record = dsl.insertInto(TAKING_MEDICATION)
                .set(TAKING_MEDICATION.PATIENT_ID, takingMedication.getPatient_id())
                .set(TAKING_MEDICATION.MEDICINE_ID, takingMedication.getMedicine_id())
                .set(TAKING_MEDICATION.DATE_TIME, takingMedication.getDate_time())
                .returning(TAKING_MEDICATION.ID)
                .fetchOne();

        if (record != null) {
            takingMedication.setId(record.get(TAKING_MEDICATION.ID));
        }
        return takingMedication;
    }

    public List<TakingMedicationView> getTakingMedicineViewByPatient(
            String oms, Integer patientId, LocalDate startDate, LocalDate endDate) {
        return dsl.select(TAKING_MEDICATION.PATIENT_ID, PATIENTS.OMS, TAKING_MEDICATION.MEDICINE_ID,
                        MEDICINE.NAME, MEDICINE.MKG, TAKING_MEDICATION.DATE_TIME)
                .from(TAKING_MEDICATION)
                .join(PATIENTS).on(TAKING_MEDICATION.PATIENT_ID.eq(PATIENTS.ID))
                .join(MEDICINE).on(TAKING_MEDICATION.MEDICINE_ID.eq(MEDICINE.ID))
                .where(
                        PATIENTS.OMS.eq(oms)
                                .or(PATIENTS.ID.eq(patientId))
                                .and(TAKING_MEDICATION.DATE_TIME.between(startDate.atStartOfDay(),
                                        endDate.plusDays(1).atStartOfDay().minusSeconds(1))))
                .orderBy(TAKING_MEDICATION.DATE_TIME.desc())
                .fetchInto(TakingMedicationView.class);
    }

}
