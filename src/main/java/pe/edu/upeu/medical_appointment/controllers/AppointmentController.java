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

    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Get all appointments")
    @GetMapping
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAll() {
        log.info("GET: all appointments");

        return ResponseEntity.ok(
                toResponseList(appointmentService.getAll())
        );
    }

    @Operation(summary = "Get appointment by id")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto.AppointmentResponse> findById(@PathVariable Long id) {
        log.info("GET: appointment with id {}", id);

        return ResponseEntity.ok(
                toResponse(appointmentService.findById(id))
        );
    }

    @Operation(summary = "Get appointments by doctor")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getByDoctorId(
            @PathVariable Long doctorId
    ) {
        log.info("GET: appointments by doctor {}", doctorId);

        return ResponseEntity.ok(
                toResponseList(appointmentService.getByDoctorId(doctorId))
        );
    }

    @Operation(summary = "Get appointments by patient")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getByPatientId(
            @PathVariable Long patientId
    ) {
        log.info("GET: appointments by patient {}", patientId);

        return ResponseEntity.ok(
                toResponseList(appointmentService.getByPatientId(patientId))
        );
    }

    @Operation(summary = "Get appointments by patient DNI")
    @GetMapping("/patient/dni/{dni}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getByPatientDni(
            @PathVariable String dni
    ) {
        log.info("GET: appointments by patient dni {}", dni);

        return ResponseEntity.ok(
                toResponseList(appointmentService.getByPatientDni(dni))
        );
    }

    @Operation(summary = "Get appointments by date")
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        log.info("GET: appointments by date {}", date);

        return ResponseEntity.ok(
                toResponseList(appointmentService.getByDate(date))
        );
    }

    @Operation(summary = "Get appointments by doctor and date")
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getByDoctorIdAndDate(
            @PathVariable Long doctorId,
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        log.info("GET: appointments by doctor {} and date {}", doctorId, date);

        return ResponseEntity.ok(
                toResponseList(appointmentService.getByDoctorIdAndDate(doctorId, date))
        );
    }

    @Operation(summary = "Get appointments by patient and date")
    @GetMapping("/patient/{patientId}/date/{date}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getByPatientIdAndDate(
            @PathVariable Long patientId,
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        log.info("GET: appointments by patient {} and date {}", patientId, date);

        return ResponseEntity.ok(
                toResponseList(appointmentService.getByPatientIdAndDate(patientId, date))
        );
    }

    @Operation(summary = "Get appointments by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getByStatus(
            @PathVariable Status status
    ) {
        log.info("GET: appointments by status {}", status);

        return ResponseEntity.ok(
                toResponseList(appointmentService.getByStatus(status))
        );
    }

    @Operation(summary = "Get appointments by doctor and status")
    @GetMapping("/doctor/{doctorId}/status/{status}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getByDoctorIdAndStatus(
            @PathVariable Long doctorId,
            @PathVariable Status status
    ) {
        log.info("GET: appointments by doctor {} and status {}", doctorId, status);

        return ResponseEntity.ok(
                toResponseList(appointmentService.getByDoctorIdAndStatus(doctorId, status))
        );
    }

    @Operation(summary = "Get appointments by patient and status")
    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getByPatientIdAndStatus(
            @PathVariable Long patientId,
            @PathVariable Status status
    ) {
        log.info("GET: appointments by patient {} and status {}", patientId, status);

        return ResponseEntity.ok(
                toResponseList(appointmentService.getByPatientIdAndStatus(patientId, status))
        );
    }

    @Operation(summary = "Create appointment")
    @PostMapping
    public ResponseEntity<AppointmentDto.AppointmentResponse> create(
            @Valid @RequestBody AppointmentDto.AppointmentRequest request
    ) {
        log.info("POST: appointment");

        Appointment savedAppointment = appointmentService.create(toEntity(request));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedAppointment));
    }

    @Operation(summary = "Update appointment")
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto.AppointmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentDto.AppointmentUpdateRequest request
    ) {
        log.info("PUT: appointment with id {}", id);

        Appointment updatedAppointment = appointmentService.update(id, toEntity(request));

        return ResponseEntity.ok(toResponse(updatedAppointment));
    }

    @Operation(summary = "Cancel appointment")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDto.AppointmentStatusResponse> cancel(
            @PathVariable Long id
    ) {
        log.info("PATCH: cancel appointment with id {}", id);

        return changeStatus(
                id,
                appointmentService::cancel,
                "La cita fue cancelada correctamente"
        );
    }

    @Operation(summary = "Confirm appointment")
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentDto.AppointmentStatusResponse> confirm(
            @PathVariable Long id
    ) {
        log.info("PATCH: confirm appointment with id {}", id);

        return changeStatus(
                id,
                appointmentService::confirm,
                "La cita fue confirmada correctamente"
        );
    }

    @Operation(summary = "Complete appointment")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentDto.AppointmentStatusResponse> complete(
            @PathVariable Long id
    ) {
        log.info("PATCH: complete appointment with id {}", id);

        return changeStatus(
                id,
                appointmentService::complete,
                "La cita fue completada correctamente"
        );
    }

    private ResponseEntity<AppointmentDto.AppointmentStatusResponse> changeStatus(
            Long id,
            Function<Long, Appointment> statusAction,
            String message
    ) {
        Status previousStatus = appointmentService.findById(id).getStatus();

        Appointment updatedAppointment = statusAction.apply(id);

        return ResponseEntity.ok(
                new AppointmentDto.AppointmentStatusResponse(
                        updatedAppointment.getId(),
                        previousStatus,
                        updatedAppointment.getStatus(),
                        message,
                        LocalDateTime.now()
                )
        );
    }

    private Appointment toEntity(AppointmentDto.AppointmentRequest request) {
        return buildAppointment(
                request.reason(),
                request.appointmentDate(),
                request.startTime(),
                request.endTime(),
                request.patientId(),
                request.doctorId()
        );
    }

    private Appointment toEntity(AppointmentDto.AppointmentUpdateRequest request) {
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

    private List<AppointmentDto.AppointmentResponse> toResponseList(
            List<Appointment> appointments
    ) {
        return appointments
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AppointmentDto.AppointmentResponse toResponse(Appointment appointment) {
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();

        Long specialityId = null;
        String specialityName = null;

        if (doctor != null && doctor.getSpeciality() != null) {
            specialityId = doctor.getSpeciality().getId();
            specialityName = doctor.getSpeciality().getName();
        }

        return new AppointmentDto.AppointmentResponse(
                appointment.getId(),
                appointment.getReason(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                patient != null ? patient.getId() : null,
                patient != null ? buildFullName(patient.getName(), patient.getLastName()) : null,
                patient != null ? patient.getDni() : null,
                doctor != null ? doctor.getId() : null,
                doctor != null ? buildFullName(doctor.getName(), doctor.getLastName()) : null,
                specialityId,
                specialityName
        );
    }

    private String buildFullName(String name, String lastName) {
        String safeName = name != null ? name.trim() : "";
        String safeLastName = lastName != null ? lastName.trim() : "";

        String fullName = (safeName + " " + safeLastName).trim();

        return fullName.isEmpty() ? null : fullName;
    }
}