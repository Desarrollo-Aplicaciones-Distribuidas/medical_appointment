package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.dtos.AppointmentDto;
import pe.edu.upeu.medical_appointment.entity.Appointment;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.entity.Patient;
import pe.edu.upeu.medical_appointment.entity.Status;
import pe.edu.upeu.medical_appointment.services.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private static final Logger log =
            LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService
    ) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Get all appointments")
    @GetMapping
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAllAppointments() {
        log.info("REST request to get all appointments");

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService.getAll()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointment by id")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto.AppointmentResponse> getAppointmentById(
            @PathVariable Long id
    ) {
        Appointment appointment =
                appointmentService.getById(id);

        return ResponseEntity.ok(
                toResponse(appointment)
        );
    }

    @Operation(summary = "Get appointments by doctor")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAppointmentsByDoctor(
            @PathVariable Long doctorId
    ) {
        log.info(
                "REST request to get appointments for doctor {}",
                doctorId
        );

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService.getByDoctorId(doctorId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointments by patient")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAppointmentsByPatient(
            @PathVariable Long patientId
    ) {

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService.getByPatientId(patientId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointments by patient DNI")
    @GetMapping("/patient/dni/{dni}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAppointmentsByPatientDni(
            @PathVariable String dni
    ) {
        log.info(
                "REST request to get appointments for patient with dni {}",
                dni
        );

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService.getByPatientDni(dni)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointments by date")
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAppointmentsByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        log.info(
                "REST request to get appointments for date {}",
                date
        );

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService.getByDate(date)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointments by doctor and date")
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAppointmentsByDoctorAndDate(
            @PathVariable Long doctorId,
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        log.info(
                "REST request to get appointments for doctor {} and date {}",
                doctorId,
                date
        );

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService
                        .getByDoctorIdAndDate(
                                doctorId,
                                date
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointments by patient and date")
    @GetMapping("/patient/{patientId}/date/{date}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAppointmentsByPatientAndDate(
            @PathVariable Long patientId,
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        log.info(
                "REST request to get appointments for patient {} and date {}",
                patientId,
                date
        );

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService
                        .getByPatientIdAndDate(
                                patientId,
                                date
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointments by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAppointmentsByStatus(
            @PathVariable Status status
    ) {
        log.info(
                "REST request to get appointments with status {}",
                status
        );

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService.getByStatus(status)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointments by doctor and status")
    @GetMapping("/doctor/{doctorId}/status/{status}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAppointmentsByDoctorAndStatus(
            @PathVariable Long doctorId,
            @PathVariable Status status
    ) {
        log.info(
                "REST request to get appointments for doctor {} with status {}",
                doctorId,
                status
        );

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService
                        .getByDoctorIdAndStatus(
                                doctorId,
                                status
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointments by patient and status")
    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAppointmentsByPatientAndStatus(
            @PathVariable Long patientId,
            @PathVariable Status status
    ) {
        log.info(
                "REST request to get appointments for patient {} with status {}",
                patientId,
                status
        );

        List<AppointmentDto.AppointmentResponse> response =
                appointmentService
                        .getByPatientIdAndStatus(
                                patientId,
                                status
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create appointment")
    @PostMapping
    public ResponseEntity<AppointmentDto.AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentDto.AppointmentRequest request
    ) {
        log.info("REST request to create appointment");

        Appointment appointment =
                toEntity(request);

        Appointment savedAppointment =
                appointmentService.create(appointment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedAppointment));
    }

    @Operation(summary = "Update appointment")
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto.AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentDto.AppointmentUpdateRequest request
    ) {
        log.info(
                "REST request to update appointment with id {}",
                id
        );

        Appointment appointment =
                toEntity(request);

        Appointment updatedAppointment =
                appointmentService.update(
                        id,
                        appointment
                );

        return ResponseEntity.ok(
                toResponse(updatedAppointment)
        );
    }

    @Operation(summary = "Cancel appointment")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDto.AppointmentStatusResponse> cancelAppointment(
            @PathVariable Long id
    ) {
        log.info(
                "REST request to cancel appointment with id {}",
                id
        );

        return changeAppointmentStatus(
                id,
                appointmentService::cancel,
                "La cita fue cancelada correctamente"
        );
    }

    @Operation(summary = "Confirm appointment")
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentDto.AppointmentStatusResponse> confirmAppointment(
            @PathVariable Long id
    ) {
        log.info(
                "REST request to confirm appointment with id {}",
                id
        );

        return changeAppointmentStatus(
                id,
                appointmentService::confirm,
                "La cita fue confirmada correctamente"
        );
    }

    @Operation(summary = "Complete appointment")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentDto.AppointmentStatusResponse> completeAppointment(
            @PathVariable Long id
    ) {
        log.info(
                "REST request to complete appointment with id {}",
                id
        );

        return changeAppointmentStatus(
                id,
                appointmentService::complete,
                "La cita fue completada correctamente"
        );
    }

    private ResponseEntity<AppointmentDto.AppointmentStatusResponse> changeAppointmentStatus(
            Long id,
            Function<Long, Appointment> statusAction,
            String message
    ) {
        Status previousStatus =
                appointmentService.getById(id).getStatus();

        Appointment updatedAppointment =
                statusAction.apply(id);

        return ResponseEntity.ok(
                toStatusResponse(
                        updatedAppointment,
                        previousStatus,
                        message
                )
        );
    }

    private Appointment toEntity(
            AppointmentDto.AppointmentRequest request
    ) {
        return buildAppointment(
                request.reason(),
                request.appointmentDate(),
                request.startTime(),
                request.endTime(),
                request.patientId(),
                request.doctorId()
        );
    }

    private Appointment toEntity(
            AppointmentDto.AppointmentUpdateRequest request
    ) {
        return buildAppointment(
                request.reason(),
                request.appointmentDate(),
                request.startTime(),
                request.endTime(),
                request.patientId(),
                request.doctorId()
        );
    }

    private Appointment buildAppointment(
            String reason,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            Long patientId,
            Long doctorId
    ) {
        Patient patient = new Patient();
        patient.setId(patientId);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();

        appointment.setReason(reason);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(Status.SCHEDULED);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        return appointment;
    }

    private AppointmentDto.AppointmentResponse toResponse(
            Appointment appointment
    ) {
        Long patientId = null;
        String patientFullName = null;
        String patientDni = null;

        if (appointment.getPatient() != null) {
            patientId = appointment.getPatient().getId();
            patientFullName = buildFullName(
                    appointment.getPatient().getName(),
                    appointment.getPatient().getLastName()
            );
            patientDni = appointment.getPatient().getDni();
        }

        Long doctorId = null;
        String doctorFullName = null;
        Long specialityId = null;
        String specialityName = null;

        if (appointment.getDoctor() != null) {
            doctorId = appointment.getDoctor().getId();
            doctorFullName = buildFullName(
                    appointment.getDoctor().getName(),
                    appointment.getDoctor().getLastName()
            );

            if (appointment.getDoctor().getSpeciality() != null) {
                specialityId =
                        appointment.getDoctor().getSpeciality().getId();

                specialityName =
                        appointment.getDoctor().getSpeciality().getName();
            }
        }

        return new AppointmentDto.AppointmentResponse(
                appointment.getId(),
                appointment.getReason(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                patientId,
                patientFullName,
                patientDni,
                doctorId,
                doctorFullName,
                specialityId,
                specialityName
        );
    }

    private AppointmentDto.AppointmentStatusResponse toStatusResponse(
            Appointment appointment,
            Status previousStatus,
            String message
    ) {
        return new AppointmentDto.AppointmentStatusResponse(
                appointment.getId(),
                previousStatus,
                appointment.getStatus(),
                message,
                LocalDateTime.now()
        );
    }

    private String buildFullName(
            String name,
            String lastName
    ) {
        String safeName =
                name != null ? name.trim() : "";

        String safeLastName =
                lastName != null ? lastName.trim() : "";

        String fullName =
                (safeName + " " + safeLastName).trim();

        return fullName.isEmpty() ? null : fullName;
    }
}