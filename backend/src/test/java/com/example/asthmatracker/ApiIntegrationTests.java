package com.example.asthmatracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void rejectsInvalidClinicalMeasurements() throws Exception {
        mockMvc.perform(post("/api/attacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patient_id":1,"date_time":"2026-08-24T10:00:00","scale":6}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation_errors.scale").exists());

        mockMvc.perform(post("/api/spirometry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patient_id":1,"date_time":"2026-08-24T10:00:00","result":49}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation_errors.result").exists());
    }

    @Test
    void supportsOpenDateRangesAndRejectsReversedRange() throws Exception {
        int patientId = insertPatient("1000000000000001");
        insertAttack(patientId, LocalDateTime.of(2026, 8, 20, 10, 0), 2);
        insertAttack(patientId, LocalDateTime.of(2026, 8, 23, 10, 0), 4);

        mockMvc.perform(get("/api/attacks").param("patient_id", String.valueOf(patientId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/attacks")
                        .param("patient_id", String.valueOf(patientId))
                        .param("start_date", "2026-08-22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].scale").value(4));

        mockMvc.perform(get("/api/attacks")
                        .param("patient_id", String.valueOf(patientId))
                        .param("start_date", "2026-08-24")
                        .param("end_date", "2026-08-20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("start_date")));
    }

    @Test
    void registrationReturnsSafeResponseAndDuplicateIsConflict() throws Exception {
        String oms = "1000000000000002";
        insertPatient(oms);
        String request = """
                {"oms":"%s","password":"strong-password"}
                """.formatted(oms);

        mockMvc.perform(post("/api/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.identifier").value(oms))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(content().string(not(containsString("strong-password"))));

        mockMvc.perform(post("/api/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void registrationForUnknownPatientIsNotFound() throws Exception {
        mockMvc.perform(post("/api/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"oms":"9999999999999999","password":"strong-password"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateOfUnknownPatientIsNotFoundInsteadOfServerError() throws Exception {
        mockMvc.perform(put("/api/patients/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPatientJson("1000000000000003")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createsUpdatesAndDeletesPatientWithExplicitStatuses() throws Exception {
        String oms = "1000000000000006";
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPatientJson(oms)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.phone_number").value("+79991234567"));

        int id = jdbc.queryForObject("SELECT id FROM tracker.patients WHERE oms = ?", Integer.class, oms);
        mockMvc.perform(put("/api/patients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPatientJson(oms).replace("Иванов", "Петров")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.surname").value("Петров"));

        mockMvc.perform(delete("/api/patients").param("oms", oms))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/patients").param("oms", oms))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsAndReadsClinicalMeasurements() throws Exception {
        int patientId = insertPatient("1000000000000007");

        mockMvc.perform(post("/api/attacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patient_id":%d,"date_time":"2026-08-24T10:00:00","scale":3}
                                """.formatted(patientId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.patient_id").value(patientId));

        mockMvc.perform(post("/api/spirometry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patient_id":%d,"date_time":"2026-08-24T10:05:00","result":450}
                                """.formatted(patientId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.result").value(450));

        mockMvc.perform(get("/api/spirometry").param("patient_id", String.valueOf(patientId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void malformedQueryParameterUsesApiErrorContract() throws Exception {
        mockMvc.perform(get("/api/attacks")
                        .param("patient_id", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/attacks"));
    }

    @Test
    void medicationHistoryAppliesIdentityAndDateFiltersTogether() throws Exception {
        int firstPatient = insertPatient("1000000000000004");
        int secondPatient = insertPatient("1000000000000005");
        int medicineId = insertMedicine("Сальбутамол", 100);
        insertTaking(firstPatient, medicineId, LocalDateTime.of(2026, 8, 20, 9, 0));
        insertTaking(firstPatient, medicineId, LocalDateTime.of(2026, 8, 23, 9, 0));
        insertTaking(secondPatient, medicineId, LocalDateTime.of(2026, 8, 23, 11, 0));

        mockMvc.perform(get("/api/medicine/taking-medicine")
                        .param("patient_id", String.valueOf(firstPatient))
                        .param("start_date", "2026-08-22")
                        .param("end_date", "2026-08-24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].patient_id").value(firstPatient))
                .andExpect(jsonPath("$[0].medicine_name").value("Сальбутамол"));
    }

    @Test
    void doctorPasswordIsAcceptedOnlyInPostBody() throws Exception {
        jdbc.update("""
                INSERT INTO tracker.doctors (name, surname, personnel_number)
                VALUES (?, ?, ?)
                """, "Иван", "Иванов", "DOC-001");

        mockMvc.perform(post("/api/doctors/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"personnel_number":"DOC-001","password":"strong-password"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/doctors/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"personnel_number":"DOC-001","password":"strong-password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(get("/api/doctors/validate")
                        .param("personnel_number", "DOC-001")
                        .param("password", "strong-password"))
                .andExpect(status().isMethodNotAllowed());
    }

    private int insertPatient(String oms) {
        jdbc.update("""
                INSERT INTO tracker.patients
                    (name, surname, patronymic, birthday, email, phone_number, oms, sex, height)
                VALUES (?, ?, ?, DATE '1990-01-01', ?, ?, ?, ?, ?)
                """, "Иван", "Иванов", "Иванович", "patient@example.com", "+79991234567", oms, "male", 180);
        return jdbc.queryForObject("SELECT id FROM tracker.patients WHERE oms = ?", Integer.class, oms);
    }

    private void insertAttack(int patientId, LocalDateTime dateTime, int scale) {
        jdbc.update("""
                INSERT INTO tracker.attacks_of_illness (patient_id, date_time, scale)
                VALUES (?, ?, ?)
                """, patientId, Timestamp.valueOf(dateTime), scale);
    }

    private int insertMedicine(String name, int mkg) {
        jdbc.update("INSERT INTO tracker.medicine (name, mkg) VALUES (?, ?)", name, mkg);
        return jdbc.queryForObject(
                "SELECT id FROM tracker.medicine WHERE name = ? AND mkg = ?",
                Integer.class,
                name,
                mkg
        );
    }

    private void insertTaking(int patientId, int medicineId, LocalDateTime dateTime) {
        jdbc.update("""
                INSERT INTO tracker.taking_medication (patient_id, medicine_id, date_time)
                VALUES (?, ?, ?)
                """, patientId, medicineId, Timestamp.valueOf(dateTime));
    }

    private String validPatientJson(String oms) {
        return """
                {
                  "name":"Иван",
                  "surname":"Иванов",
                  "patronymic":"Иванович",
                  "birthday":"1990-01-01",
                  "email":"patient@example.com",
                  "phone_number":"+79991234567",
                  "oms":"%s",
                  "sex":"male",
                  "height":180
                }
                """.formatted(oms);
    }
}
