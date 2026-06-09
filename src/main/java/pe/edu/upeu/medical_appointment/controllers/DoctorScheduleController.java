package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.entity.DayOfWeek;
import pe.edu.upeu.medical_appointment.entity.DoctorSchedule;

import pe.edu.upeu.medical_appointment.services.DoctorScheduleService;

import java.util.List;

@RestController
@RequestMapping("/doctor-schedules")
public class DoctorScheduleController {

    private static final Logger log =
            LoggerFactory.getLogger(DoctorScheduleController.class);

    private final DoctorScheduleService doctorScheduleService;

    public DoctorScheduleController(
            DoctorScheduleService doctorScheduleService
    ) {
        this.doctorScheduleService = doctorScheduleService;
    }

    @Operation(summary = "Get all doctor schedules")
    @GetMapping
    public ResponseEntity<List<DoctorSchedule>> getAllSchedules() {
        log.info("REST request to get all doctor schedules");

        return ResponseEntity.ok(
                doctorScheduleService.getAll()
        );
    }

    @Operation(summary = "Get doctor schedule by id")
    @GetMapping("/{id}")
    public ResponseEntity<DoctorSchedule> getScheduleById(
            @PathVariable Long id
    ) {
        log.info("REST request to get doctor schedule with id {}", id);

        return ResponseEntity.ok(
                doctorScheduleService.getById(id)
        );
    }

    @Operation(summary = "Get schedules by doctor id")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorSchedule>> getSchedulesByDoctor(
            @PathVariable Long doctorId
    ) {
        log.info(
                "REST request to get schedules for doctor {}",
                doctorId
        );

        return ResponseEntity.ok(
                doctorScheduleService.getByDoctorId(doctorId)
        );
    }

    @Operation(summary = "Get schedules by doctor and day of week")
    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    public ResponseEntity<List<DoctorSchedule>> getSchedulesByDoctorAndDay(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek
    ) {
        log.info(
                "REST request to get schedules for doctor {} and day {}",
                doctorId,
                dayOfWeek
        );

        return ResponseEntity.ok(
                doctorScheduleService
                        .getByDoctorIdAndDayOfWeek(
                                doctorId,
                                dayOfWeek
                        )
        );
    }

    @Operation(summary = "Create doctor schedule")
    @PostMapping
    public ResponseEntity<DoctorSchedule> createSchedule(
            @RequestBody DoctorSchedule doctorSchedule
    ) {
        log.info("REST request to create doctor schedule");

        return ResponseEntity.ok(
                doctorScheduleService.create(doctorSchedule)
        );
    }

    @Operation(summary = "Update doctor schedule")
    @PutMapping("/{id}")
    public ResponseEntity<DoctorSchedule> updateSchedule(
            @PathVariable Long id,
            @RequestBody DoctorSchedule doctorSchedule
    ) {
        log.info(
                "REST request to update doctor schedule with id {}",
                id
        );

        return ResponseEntity.ok(
                doctorScheduleService.update(
                        id,
                        doctorSchedule
                )
        );
    }

    @Operation(summary = "Delete doctor schedule")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id
    ) {
        log.info(
                "REST request to delete doctor schedule with id {}",
                id
        );

        doctorScheduleService.delete(id);

        return ResponseEntity.noContent().build();
    }
}