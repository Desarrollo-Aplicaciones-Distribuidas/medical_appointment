package pe.edu.upeu.medical_appointment.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.upeu.medical_appointment.entity.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentDto() {

    public record AppointmentRequest(
            @NotBlank(message = "Reason is required")
            @Size(max = 500, message = "Reason must have at most 500 characters")
            String reason,

            @NotNull(message = "Appointment date is required")
            LocalDate appointmentDate,

            @NotNull(message = "Start time is required")
            LocalTime startTime,

            @NotNull(message = "End time is required")
            LocalTime endTime,

            @NotNull(message = "Patient id is required")
            Long patientId,

            @NotNull(message = "Doctor id is required")
            Long doctorId
    ) {
    }

    public record AppointmentUpdateRequest(
            @NotBlank(message = "Reason is required")
            @Size(max = 500, message = "Reason must have at most 500 characters")
            String reason,

            @NotNull(message = "Appointment date is required")
            LocalDate appointmentDate,

            @NotNull(message = "Start time is required")
            LocalTime startTime,

            @NotNull(message = "End time is required")
            LocalTime endTime,

            @NotNull(message = "Patient id is required")
            Long patientId,

            @NotNull(message = "Doctor id is required")
            Long doctorId
    ) {
    }

    public record AppointmentResponse(
            Long id,
            String reason,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            Status status,

            Long patientId,
            String patientFullName,
            String patientDni,

            Long doctorId,
            String doctorFullName,

            Long specialityId,
            String specialityName
    ) {
    }

    public record AppointmentComboResponse(
            Long id,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            Status status,
            String patientFullName,
            String doctorFullName
    ) {
    }

    public record AppointmentStatusResponse(
            Long id,
            Status previousStatus,
            Status currentStatus,
            String message,
            LocalDateTime changedAt
    ) {
    }
}