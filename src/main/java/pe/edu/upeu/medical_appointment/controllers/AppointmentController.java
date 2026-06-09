package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.entity.Appointment;
import pe.edu.upeu.medical_appointment.entity.Status;
import pe.edu.upeu.medical_appointment.services.AppointmentService;

import java.time.LocalDate;
import java.util.List;

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
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        log.info("REST request to get all appointments");

        return ResponseEntity.ok(
                appointmentService.getAll()
        );
    }

    @Operation(summary = "Get appointment by id")
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(
            @PathVariable Long id
    ) {
        log.info(
                "REST request to get appointment with id {}",
                id
        );

        return ResponseEntity.ok(
                appointmentService.getById(id)
        );
    }

    @Operation(summary = "Get appointments by doctor")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctor(
            @PathVariable Long doctorId
    ) {
        log.info(
                "REST request to get appointments for doctor {}",
                doctorId
        );

        return ResponseEntity.ok(
                appointmentService.getByDoctorId(doctorId)
        );
    }

    @Operation(summary = "Get appointments by patient")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(
            @PathVariable Long patientId
    ) {
        log.info(
                "REST request to get appointments for patient {}",
                patientId
        );

        return ResponseEntity.ok(
                appointmentService.getByPatientId(patientId)
        );
    }

    @Operation(summary = "Get appointments by date")
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDate(
            @PathVariable LocalDate date
    ) {
        log.info(
                "REST request to get appointments for date {}",
                date
        );

        return ResponseEntity.ok(
                appointmentService.getByDate(date)
        );
    }

    @Operation(summary = "Get appointments by doctor and date")
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorAndDate(
            @PathVariable Long doctorId,
            @PathVariable LocalDate date
    ) {
        log.info(
                "REST request to get appointments for doctor {} and date {}",
                doctorId,
                date
        );

        return ResponseEntity.ok(
                appointmentService.getByDoctorIdAndDate(
                        doctorId,
                        date
                )
        );
    }

    @Operation(summary = "Get appointments by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(
            @PathVariable Status status
    ) {
        log.info(
                "REST request to get appointments with status {}",
                status
        );

        return ResponseEntity.ok(
                appointmentService.getByStatus(status)
        );
    }

    @Operation(summary = "Create appointment")
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Appointment appointment
    ) {
        log.info("REST request to create appointment");

        return ResponseEntity.ok(
                appointmentService.create(appointment)
        );
    }

    @Operation(summary = "Update appointment")
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable Long id,
            @RequestBody Appointment appointment
    ) {
        log.info(
                "REST request to update appointment with id {}",
                id
        );

        return ResponseEntity.ok(
                appointmentService.update(
                        id,
                        appointment
                )
        );
    }

    @Operation(summary = "Delete appointment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable Long id
    ) {
        log.info(
                "REST request to delete appointment with id {}",
                id
        );

        appointmentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}