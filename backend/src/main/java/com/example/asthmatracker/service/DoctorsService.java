package com.example.asthmatracker.service;

import com.example.asthmatracker.models.Doctors;
import com.example.asthmatracker.models.DoctorsRegistration;
import com.example.asthmatracker.models.RegistrationResponse;
import com.example.asthmatracker.web.NotFoundException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.asthmatracker.persistence.DatabaseTables.*;

@Service
public class DoctorsService {

    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;

    public DoctorsService(DSLContext dsl, PasswordEncoder passwordEncoder) {
        this.dsl = dsl;
        this.passwordEncoder = passwordEncoder;
    }

    public Doctors create(Doctors doctor) {
        Integer id = dsl.insertInto(DOCTORS)
                .set(DOCTOR_NAME, doctor.name())
                .set(DOCTOR_SURNAME, doctor.surname())
                .set(DOCTOR_PERSONNEL_NUMBER, doctor.personnelNumber())
                .returning(DOCTOR_ID)
                .fetchOne(DOCTOR_ID);
        return new Doctors(id, doctor.name(), doctor.surname(), doctor.personnelNumber());
    }

    public Doctors getByPersonnelNumber(String personnelNumber) {
        Record record = dsl.select(DOCTOR_ID, DOCTOR_NAME, DOCTOR_SURNAME, DOCTOR_PERSONNEL_NUMBER)
                .from(DOCTORS)
                .where(DOCTOR_PERSONNEL_NUMBER.eq(personnelNumber))
                .fetchOne();
        if (record == null) {
            throw new NotFoundException("Doctor not found: " + personnelNumber);
        }
        return new Doctors(
                record.get(DOCTOR_ID),
                record.get(DOCTOR_NAME),
                record.get(DOCTOR_SURNAME),
                record.get(DOCTOR_PERSONNEL_NUMBER)
        );
    }

    public RegistrationResponse register(DoctorsRegistration request) {
        if (!dsl.fetchExists(DOCTORS, DOCTOR_PERSONNEL_NUMBER.eq(request.personnelNumber()))) {
            throw new NotFoundException("Doctor not found: " + request.personnelNumber());
        }
        Integer id = dsl.insertInto(DOCTOR_LOGIN)
                .set(DOCTOR_LOGIN_PERSONNEL_NUMBER, request.personnelNumber())
                .set(DOCTOR_LOGIN_PASSWORD, passwordEncoder.encode(request.password()))
                .returning(DOCTOR_LOGIN_ID)
                .fetchOne(DOCTOR_LOGIN_ID);
        return new RegistrationResponse(id, request.personnelNumber());
    }

    public boolean isLoginValid(String personnelNumber, String password) {
        String hash = dsl.select(DOCTOR_LOGIN_PASSWORD)
                .from(DOCTOR_LOGIN)
                .where(DOCTOR_LOGIN_PERSONNEL_NUMBER.eq(personnelNumber))
                .fetchOne(DOCTOR_LOGIN_PASSWORD);
        return hash != null && passwordEncoder.matches(password, hash);
    }
}
