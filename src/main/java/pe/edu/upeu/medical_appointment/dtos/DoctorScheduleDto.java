package pe.edu.upeu.medical_appointment.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.medical_appointment.entity.DayOfWeek;

import java.time.LocalTime;

public record DoctorScheduleDto() {

    public record DoctorScheduleRequest(
            @NotNull(message = "Day of week is required")
            DayOfWeek dayOfWeek,

            @NotNull(message = "Start time is required")
            LocalTime startTime,

            @NotNull(message = "End time is required")
            LocalTime endTime,

            @NotNull(message = "Appointment duration is required")
            @Min(value = 1, message = "Appointment duration must be greater than zero")
            Integer appointmentDuration,

            @NotNull(message = "Doctor id is required")
            Long doctorId
    ) {
    }

    public record DoctorScheduleResponse(
            Long id,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            Integer appointmentDuration,
            Long doctorId,
            String doctorFullName
    ) {
    }

    public record DoctorScheduleComboResponse(
            Long id,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            Integer appointmentDuration
    ) {
    }
}