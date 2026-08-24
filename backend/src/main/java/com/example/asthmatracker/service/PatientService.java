package com.example.asthmatracker.service;

import com.example.asthmatracker.models.Patient;
import com.example.asthmatracker.models.PatientRegistration;
import com.example.asthmatracker.models.RegistrationResponse;
import com.example.asthmatracker.web.NotFoundException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.asthmatracker.persistence.DatabaseTables.*;

@Service
public class PatientService {

    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;

    public PatientService(DSLContext dsl, PasswordEncoder passwordEncoder) {
        this.dsl = dsl;
        this.passwordEncoder = passwordEncoder;
    }

    public Patient create(Patient patient) {
        Integer id = dsl.insertInto(PATIENTS)
                .set(PATIENT_NAME, patient.name())
                .set(PATIENT_SURNAME, patient.surname())
                .set(PATIENT_PATRONYMIC, patient.patronymic())
                .set(PATIENT_BIRTHDAY, patient.birthday())
                .set(PATIENT_EMAIL, patient.email())
                .set(PATIENT_PHONE, patient.phoneNumber())
                .set(PATIENT_OMS, patient.oms())
                .set(PATIENT_SEX, patient.sex())
                .set(PATIENT_HEIGHT, patient.height())
                .returning(PATIENT_ID)
                .fetchOne(PATIENT_ID);
        return copyWithId(patient, id);
    }

    public List<Patient> find(String fullName, String oms) {
        Condition condition = DSL.noCondition();
        if (fullName != null && !fullName.isBlank()) {
            String pattern = "%" + fullName.trim().toLowerCase() + "%";
            condition = condition.and(
                    DSL.lower(PATIENT_NAME).like(pattern)
                            .or(DSL.lower(PATIENT_SURNAME).like(pattern))
                            .or(DSL.lower(PATIENT_PATRONYMIC).like(pattern))
            );
        }
        if (oms != null && !oms.isBlank()) {
            condition = condition.and(PATIENT_OMS.eq(oms.trim()));
        }

        return dsl.select(patientFields())
                .from(PATIENTS)
                .where(condition)
                .orderBy(PATIENT_SURNAME, PATIENT_NAME)
                .fetch(this::map);
    }

    public Patient update(Integer id, Patient patient) {
        Record record = dsl.update(PATIENTS)
                .set(PATIENT_NAME, patient.name())
                .set(PATIENT_SURNAME, patient.surname())
                .set(PATIENT_PATRONYMIC, patient.patronymic())
                .set(PATIENT_BIRTHDAY, patient.birthday())
                .set(PATIENT_EMAIL, patient.email())
                .set(PATIENT_PHONE, patient.phoneNumber())
                .set(PATIENT_OMS, patient.oms())
                .set(PATIENT_SEX, patient.sex())
                .set(PATIENT_HEIGHT, patient.height())
                .where(PATIENT_ID.eq(id))
                .returning(patientFields())
                .fetchOne();
        if (record == null) {
            throw new NotFoundException("Patient not found: " + id);
        }
        return map(record);
    }

    public void deleteByOms(String oms) {
        int deleted = dsl.deleteFrom(PATIENTS)
                .where(PATIENT_OMS.eq(oms))
                .execute();
        if (deleted == 0) {
            throw new NotFoundException("Patient not found for OMS: " + oms);
        }
    }

    public RegistrationResponse register(PatientRegistration request) {
        if (!dsl.fetchExists(PATIENTS, PATIENT_OMS.eq(request.oms()))) {
            throw new NotFoundException("Patient not found for OMS: " + request.oms());
        }
        Integer id = dsl.insertInto(PATIENT_LOGIN)
                .set(PATIENT_LOGIN_OMS, request.oms())
                .set(PATIENT_LOGIN_PASSWORD, passwordEncoder.encode(request.password()))
                .returning(PATIENT_LOGIN_ID)
                .fetchOne(PATIENT_LOGIN_ID);
        return new RegistrationResponse(id, request.oms());
    }

    public boolean isLoginValid(String oms, String password) {
        String hash = dsl.select(PATIENT_LOGIN_PASSWORD)
                .from(PATIENT_LOGIN)
                .where(PATIENT_LOGIN_OMS.eq(oms))
                .fetchOne(PATIENT_LOGIN_PASSWORD);
        return hash != null && passwordEncoder.matches(password, hash);
    }

    private org.jooq.SelectFieldOrAsterisk[] patientFields() {
        return new org.jooq.SelectFieldOrAsterisk[]{
                PATIENT_ID, PATIENT_NAME, PATIENT_SURNAME, PATIENT_PATRONYMIC,
                PATIENT_BIRTHDAY, PATIENT_EMAIL, PATIENT_PHONE, PATIENT_OMS,
                PATIENT_SEX, PATIENT_HEIGHT
        };
    }

    private Patient map(Record record) {
        return new Patient(
                record.get(PATIENT_ID), record.get(PATIENT_NAME), record.get(PATIENT_SURNAME),
                record.get(PATIENT_PATRONYMIC), record.get(PATIENT_BIRTHDAY), record.get(PATIENT_EMAIL),
                record.get(PATIENT_PHONE), record.get(PATIENT_OMS), record.get(PATIENT_SEX),
                record.get(PATIENT_HEIGHT)
        );
    }

    private Patient copyWithId(Patient patient, Integer id) {
        return new Patient(id, patient.name(), patient.surname(), patient.patronymic(), patient.birthday(),
                patient.email(), patient.phoneNumber(), patient.oms(), patient.sex(), patient.height());
    }
}
