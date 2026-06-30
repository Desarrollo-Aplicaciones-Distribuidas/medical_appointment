package pe.edu.upeu.medical_appointment.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "appointments",
        indexes = {
                @Index(name = "idx_appointments_patient_id", columnList = "patient_id"),
                @Index(name = "idx_appointments_doctor_id", columnList = "doctor_id"),
                @Index(name = "idx_appointments_date", columnList = "appointment_date"),
                @Index(name = "idx_appointments_status", columnList = "status"),
                @Index(name = "idx_appointments_doctor_date", columnList = "doctor_id, appointment_date"),
                @Index(name = "idx_appointments_patient_date", columnList = "patient_id, appointment_date")
        }
)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false, name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(nullable = false, name = "start_time")
    private LocalTime startTime;

    @Column(nullable = false, name = "end_time")
    private LocalTime endTime;

    @Column(nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    public Appointment() {
    }

    public Appointment(
            Long id,
            String reason,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            Status status,
            Patient patient,
            Doctor doctor
    ) {
        this.id = id;
        this.reason = reason;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.patient = patient;
        this.doctor = doctor;
    }

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = Status.SCHEDULED;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}