package com.example.asthmatracker.persistence;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DatabaseTables {

    public static final Table<Record> PATIENTS = table("patients");
    public static final Field<Integer> PATIENT_ID = integer("id");
    public static final Field<String> PATIENT_NAME = varchar("name");
    public static final Field<String> PATIENT_SURNAME = varchar("surname");
    public static final Field<String> PATIENT_PATRONYMIC = varchar("patronymic");
    public static final Field<LocalDate> PATIENT_BIRTHDAY = date("birthday");
    public static final Field<String> PATIENT_EMAIL = varchar("email");
    public static final Field<String> PATIENT_PHONE = varchar("phone_number");
    public static final Field<String> PATIENT_OMS = varchar("oms");
    public static final Field<String> PATIENT_SEX = varchar("sex");
    public static final Field<Integer> PATIENT_HEIGHT = integer("height");

    public static final Table<Record> PATIENT_LOGIN = table("patient_login");
    public static final Field<Integer> PATIENT_LOGIN_ID = integer("id");
    public static final Field<String> PATIENT_LOGIN_OMS = varchar("oms");
    public static final Field<String> PATIENT_LOGIN_PASSWORD = varchar("password");

    public static final Table<Record> DOCTORS = table("doctors");
    public static final Field<Integer> DOCTOR_ID = integer("id");
    public static final Field<String> DOCTOR_NAME = varchar("name");
    public static final Field<String> DOCTOR_SURNAME = varchar("surname");
    public static final Field<String> DOCTOR_PERSONNEL_NUMBER = varchar("personnel_number");

    public static final Table<Record> DOCTOR_LOGIN = table("doctor_login");
    public static final Field<Integer> DOCTOR_LOGIN_ID = integer("id");
    public static final Field<String> DOCTOR_LOGIN_PERSONNEL_NUMBER = varchar("personnel_number");
    public static final Field<String> DOCTOR_LOGIN_PASSWORD = varchar("password");

    public static final Table<Record> ATTACKS = table("attacks_of_illness");
    public static final Field<Integer> ATTACK_ID = integer("id");
    public static final Field<Integer> ATTACK_PATIENT_ID = integer("patient_id");
    public static final Field<LocalDateTime> ATTACK_DATE_TIME = timestamp("date_time");
    public static final Field<Integer> ATTACK_SCALE = integer("scale");

    public static final Table<Record> SPIROMETRY = table("spirometry");
    public static final Field<Integer> SPIROMETRY_ID = integer("id");
    public static final Field<Integer> SPIROMETRY_PATIENT_ID = integer("patient_id");
    public static final Field<Integer> SPIROMETRY_RESULT = integer("result");
    public static final Field<LocalDateTime> SPIROMETRY_DATE_TIME = timestamp("date_time");

    public static final Table<Record> MEDICINE = table("medicine");
    public static final Field<Integer> MEDICINE_ID = integer("id");
    public static final Field<String> MEDICINE_NAME = varchar("name");
    public static final Field<Integer> MEDICINE_MKG = integer("mkg");

    public static final Table<Record> MEDICINE_TO_PATIENT = table("medicine_to_patient");
    public static final Field<Integer> ASSIGNMENT_PATIENT_ID = integer("patient_id");
    public static final Field<Integer> ASSIGNMENT_MEDICINE_ID = integer("medicine_id");

    public static final Table<Record> TAKING_MEDICATION = table("taking_medication");
    public static final Field<Integer> TAKING_ID = integer("id");
    public static final Field<Integer> TAKING_PATIENT_ID = integer("patient_id");
    public static final Field<Integer> TAKING_MEDICINE_ID = integer("medicine_id");
    public static final Field<LocalDateTime> TAKING_DATE_TIME = timestamp("date_time");

    private DatabaseTables() {
    }

    private static Table<Record> table(String name) {
        return DSL.table(DSL.name("tracker", name));
    }

    private static Field<Integer> integer(String name) {
        return DSL.field(DSL.name(name), SQLDataType.INTEGER);
    }

    private static Field<String> varchar(String name) {
        return DSL.field(DSL.name(name), SQLDataType.VARCHAR);
    }

    private static Field<LocalDate> date(String name) {
        return DSL.field(DSL.name(name), SQLDataType.LOCALDATE);
    }

    private static Field<LocalDateTime> timestamp(String name) {
        return DSL.field(DSL.name(name), SQLDataType.LOCALDATETIME);
    }
}
