package com.example.asthmatracker.service;

import com.example.asthmatracker.models.Doctors;
import com.example.asthmatracker.models.DoctorsRegistration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.jooq.generated.Tables.DOCTORS;
import static com.example.jooq.generated.Tables.DOCTOR_LOGIN;

@Service
public class DoctorsService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final DSLContext dsl;

    public DoctorsService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Doctors createDoctor(Doctors doctors) {
        Record record = dsl.insertInto(DOCTORS)
                .set(DOCTORS.NAME, doctors.getName())
                .set(DOCTORS.SURNAME, doctors.getSurname())
                .set(DOCTORS.PERSONNEL_NUMBER, doctors.getPersonnel_number())
                .returning(DOCTORS.ID)
                .fetchOne();

        if (record != null) {
            doctors.setId(record.get(DOCTORS.ID));
        }

        return doctors;
    }

    public Doctors getDoctorByPersonnelNumber(String personnelNumber) {
        return dsl.selectFrom(DOCTORS)
                .where(DOCTORS.PERSONNEL_NUMBER.eq(personnelNumber))
                .fetchOneInto(Doctors.class);
    }

    public DoctorsRegistration createDoctorsPassword(DoctorsRegistration doctorsRegistration) {
        String hashedPassword = passwordEncoder.encode(doctorsRegistration.getPassword());

        Record record = dsl.insertInto(DOCTOR_LOGIN)
                .set(DOCTOR_LOGIN.PERSONNEL_NUMBER, doctorsRegistration.getPersonnel_number())
                .set(DOCTOR_LOGIN.PASSWORD, hashedPassword)
                .returning(DOCTOR_LOGIN.ID)
                .fetchOne();

        if (record != null) {
            doctorsRegistration.setId(record.get(DOCTOR_LOGIN.ID));
        }

        return null;
    }

    public boolean isLoginValid(String personnelNumber, String password) {
        Record record = dsl.selectFrom(DOCTOR_LOGIN)
                .where(DOCTOR_LOGIN.PERSONNEL_NUMBER.eq(personnelNumber))
                .fetchOne();

        if (record == null) {
            return false;
        }

        String hashedPassword = record.get(DOCTOR_LOGIN.PASSWORD);
        return passwordEncoder.matches(password, hashedPassword);
    }

    public DoctorsRegistration createDoctorPassword(DoctorsRegistration doctorsRegistration) {
        String hashedPassword = passwordEncoder.encode(doctorsRegistration.getPassword());

        Record record = dsl.insertInto(DOCTOR_LOGIN)
                .set(DOCTOR_LOGIN.PERSONNEL_NUMBER, doctorsRegistration.getPersonnel_number())
                .set(DOCTOR_LOGIN.PASSWORD, hashedPassword)
                .returning(DOCTOR_LOGIN.ID)
                .fetchOne();

        if (record != null) {
            doctorsRegistration.setId(record.get(DOCTOR_LOGIN.ID));
        }

        return null;
    }
}
