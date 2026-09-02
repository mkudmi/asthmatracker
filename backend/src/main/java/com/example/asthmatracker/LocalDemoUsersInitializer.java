package com.example.asthmatracker;

import org.jooq.DSLContext;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static com.example.asthmatracker.persistence.DatabaseTables.DOCTORS;
import static com.example.asthmatracker.persistence.DatabaseTables.DOCTOR_LOGIN;
import static com.example.asthmatracker.persistence.DatabaseTables.DOCTOR_LOGIN_PASSWORD;
import static com.example.asthmatracker.persistence.DatabaseTables.DOCTOR_LOGIN_PERSONNEL_NUMBER;
import static com.example.asthmatracker.persistence.DatabaseTables.DOCTOR_NAME;
import static com.example.asthmatracker.persistence.DatabaseTables.DOCTOR_PERSONNEL_NUMBER;
import static com.example.asthmatracker.persistence.DatabaseTables.DOCTOR_SURNAME;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENTS;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_BIRTHDAY;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_EMAIL;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_HEIGHT;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_LOGIN;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_LOGIN_OMS;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_LOGIN_PASSWORD;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_NAME;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_OMS;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_PHONE;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_SEX;
import static com.example.asthmatracker.persistence.DatabaseTables.PATIENT_SURNAME;

@Configuration
@ConditionalOnProperty(name = "app.demo-users.enabled", havingValue = "true")
public class LocalDemoUsersInitializer {

    private static final String PATIENT_OMS_VALUE = "0000000000000000";
    private static final String DOCTOR_PERSONNEL_NUMBER_VALUE = "admind";
    private static final String DEMO_PASSWORD = "admin";

    @Bean
    ApplicationRunner seedLocalDemoUsers(DSLContext dsl, PasswordEncoder passwordEncoder) {
        return args -> dsl.transaction(configuration -> {
            DSLContext tx = configuration.dsl();
            String passwordHash = passwordEncoder.encode(DEMO_PASSWORD);

            tx.insertInto(PATIENTS)
                    .set(PATIENT_NAME, "Администратор")
                    .set(PATIENT_SURNAME, "Пациент")
                    .set(PATIENT_BIRTHDAY, LocalDate.of(1990, 1, 1))
                    .set(PATIENT_EMAIL, "adminp@local.test")
                    .set(PATIENT_PHONE, "+70000000000")
                    .set(PATIENT_OMS, PATIENT_OMS_VALUE)
                    .set(PATIENT_SEX, "Не указан")
                    .set(PATIENT_HEIGHT, 170)
                    .onConflict(PATIENT_OMS)
                    .doNothing()
                    .execute();
            tx.insertInto(PATIENT_LOGIN)
                    .set(PATIENT_LOGIN_OMS, PATIENT_OMS_VALUE)
                    .set(PATIENT_LOGIN_PASSWORD, passwordHash)
                    .onConflict(PATIENT_LOGIN_OMS)
                    .doUpdate()
                    .set(PATIENT_LOGIN_PASSWORD, passwordHash)
                    .execute();

            tx.insertInto(DOCTORS)
                    .set(DOCTOR_NAME, "Администратор")
                    .set(DOCTOR_SURNAME, "Врач")
                    .set(DOCTOR_PERSONNEL_NUMBER, DOCTOR_PERSONNEL_NUMBER_VALUE)
                    .onConflict(DOCTOR_PERSONNEL_NUMBER)
                    .doNothing()
                    .execute();
            tx.insertInto(DOCTOR_LOGIN)
                    .set(DOCTOR_LOGIN_PERSONNEL_NUMBER, DOCTOR_PERSONNEL_NUMBER_VALUE)
                    .set(DOCTOR_LOGIN_PASSWORD, passwordHash)
                    .onConflict(DOCTOR_LOGIN_PERSONNEL_NUMBER)
                    .doUpdate()
                    .set(DOCTOR_LOGIN_PASSWORD, passwordHash)
                    .execute();
        });
    }
}
