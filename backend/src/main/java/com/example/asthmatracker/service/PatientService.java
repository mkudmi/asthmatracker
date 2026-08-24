package com.example.asthmatracker.service;

import com.example.asthmatracker.models.Patient;
import com.example.asthmatracker.models.PatientRegistration;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.jooq.generated.Tables.PATIENTS;
import static com.example.jooq.generated.Tables.PATIENT_LOGIN;

@Service
public class PatientService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final DSLContext dsl;

    public PatientService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Patient createPatient(Patient patient) {
        Record record = dsl.insertInto(PATIENTS)
                .set(PATIENTS.NAME, patient.getName())
                .set(PATIENTS.SURNAME, patient.getSurname())
                .set(PATIENTS.PATRONYMIC, patient.getPatronymic())
                .set(PATIENTS.BIRTHDAY, patient.getBirthday())
                .set(PATIENTS.EMAIL, patient.getEmail())
                .set(PATIENTS.PHONE_NUMBER, patient.getPhone_number())
                .set(PATIENTS.OMS, patient.getOms())
                .set(PATIENTS.SEX, patient.getSex())
                .set(PATIENTS.HEIGHT, patient.getHeight())
                .returning(PATIENTS.ID)
                .fetchOne();

        if (record != null) {
            patient.setId(record.get(PATIENTS.ID));
        }

        return patient;
    }

    public List<Patient> getPatientsByFilter(String fullName, String oms) {
        Condition condition = DSL.noCondition();
        if (fullName != null && !fullName.isBlank()) {
            String likePattern = "%" + fullName.toLowerCase() + "%";
            condition = condition.and(
                    DSL.lower(PATIENTS.NAME).like(likePattern)
                            .or(DSL.lower(PATIENTS.SURNAME).like(likePattern))
                            .or(DSL.lower(PATIENTS.PATRONYMIC).like(likePattern))
            );
        }

        if (oms != null && !oms.isBlank()) {
            condition = condition.and(
                    PATIENTS.OMS.like("%" + oms + "%")
            );
        }

        return dsl.selectFrom(PATIENTS)
                .where(condition)
                .fetchInto(Patient.class);
    }

    public Patient updatePatient(Patient patient, Integer id) {
        Record record = dsl.update(PATIENTS)
                .set(PATIENTS.NAME, patient.getName())
                .set(PATIENTS.SURNAME, patient.getSurname())
                .set(PATIENTS.PATRONYMIC, patient.getPatronymic())
                .set(PATIENTS.BIRTHDAY, patient.getBirthday())
                .set(PATIENTS.SEX, patient.getSex())
                .set(PATIENTS.EMAIL, patient.getEmail())
                .set(PATIENTS.PHONE_NUMBER, patient.getPhone_number())
                .set(PATIENTS.HEIGHT, patient.getHeight())
                .where(PATIENTS.ID.eq(id))
                .returning()
                .fetchOne();

        return record.into(Patient.class);
    }

    public void deletePatientByOms(String oms) {
        int rowsDeleted = dsl.deleteFrom(PATIENTS)
                .where(PATIENTS.OMS.eq(oms))
                .execute();
    }

    public PatientRegistration createPatientPassword(PatientRegistration patientRegistration) {
        String hashedPassword = passwordEncoder.encode(patientRegistration.getPassword());

        Record record = dsl.insertInto(PATIENT_LOGIN)
                .set(PATIENT_LOGIN.OMS, patientRegistration.getOms())
                .set(PATIENT_LOGIN.PASSWORD, hashedPassword)
                .returning(PATIENT_LOGIN.ID)
                .fetchOne();

        if (record != null) {
            patientRegistration.setId(record.get(PATIENTS.ID));
        }

        return null;
    }

    public boolean isLoginValid(String oms, String password) {
        Record record = dsl.selectFrom(PATIENT_LOGIN)
                .where(PATIENT_LOGIN.OMS.eq(oms))
                .fetchOne();

        if (record == null) {
            return false;
        }

        String hashedPassword = record.get(PATIENT_LOGIN.PASSWORD);
        return passwordEncoder.matches(password, hashedPassword);
    }
}
