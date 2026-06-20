package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.dtos.DoctorScheduleDto;
import pe.edu.upeu.medical_appointment.entity.DayOfWeek;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.entity.DoctorSchedule;
import pe.edu.upeu.medical_appointment.services.DoctorScheduleService;
import pe.edu.upeu.medical_appointment.services.DoctorService;

import java.util.List;

@RestController
@RequestMapping("/doctor-schedules")
public class DoctorScheduleController {

    private static final Logger log =
            LoggerFactory.getLogger(DoctorScheduleController.class);

    private final DoctorScheduleService doctorScheduleService;
    private final DoctorService doctorService;

    public DoctorScheduleController(
            DoctorScheduleService doctorScheduleService,
            DoctorService doctorService
    ) {
        this.doctorScheduleService = doctorScheduleService;
        this.doctorService = doctorService;
    }

    @Operation(summary = "Get all doctor schedules")
    @GetMapping
    public ResponseEntity<List<DoctorScheduleDto.DoctorScheduleResponse>> getAllSchedules() {
        log.info("REST request to get all doctor schedules");

        List<DoctorScheduleDto.DoctorScheduleResponse> response =
                doctorScheduleService.getAll()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get doctor schedule by id")
    @GetMapping("/{id}")
    public ResponseEntity<DoctorScheduleDto.DoctorScheduleResponse> getScheduleById(
            @PathVariable Long id
    ) {
        log.info(
                "REST request to get doctor schedule with id {}",
                id
        );

        DoctorSchedule schedule =
                doctorScheduleService.getById(id);

        return ResponseEntity.ok(
                toResponse(schedule)
        );
    }

    @Operation(summary = "Get schedules by doctor id")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorScheduleDto.DoctorScheduleResponse>> getSchedulesByDoctor(
            @PathVariable Long doctorId
    ) {
        log.info(
                "REST request to get schedules for doctor {}",
                doctorId
        );

        List<DoctorScheduleDto.DoctorScheduleResponse> response =
                doctorScheduleService.getByDoctorId(doctorId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get schedules by doctor and day of week")
    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    public ResponseEntity<List<DoctorScheduleDto.DoctorScheduleResponse>> getSchedulesByDoctorAndDay(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek
    ) {
        log.info(
                "REST request to get schedules for doctor {} and day {}",
                doctorId,
                dayOfWeek
        );

        List<DoctorScheduleDto.DoctorScheduleResponse> response =
                doctorScheduleService
                        .getByDoctorIdAndDayOfWeek(
                                doctorId,
                                dayOfWeek
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get schedule options by doctor and day of week")
    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}/combo")
    public ResponseEntity<List<DoctorScheduleDto.DoctorScheduleComboResponse>> getScheduleComboByDoctorAndDay(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek
    ) {
        log.info(
                "REST request to get schedule combo for doctor {} and day {}",
                doctorId,
                dayOfWeek
        );

        List<DoctorScheduleDto.DoctorScheduleComboResponse> response =
                doctorScheduleService
                        .getByDoctorIdAndDayOfWeek(
                                doctorId,
                                dayOfWeek
                        )
                        .stream()
                        .map(this::toComboResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create doctor schedule")
    @PostMapping
    public ResponseEntity<DoctorScheduleDto.DoctorScheduleResponse> createSchedule(
            @Valid @RequestBody DoctorScheduleDto.DoctorScheduleRequest request
    ) {
        log.info("REST request to create doctor schedule");

        DoctorSchedule doctorSchedule =
                toEntity(request);

        DoctorSchedule savedSchedule =
                doctorScheduleService.create(doctorSchedule);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedSchedule));
    }

    @Operation(summary = "Update doctor schedule")
    @PutMapping("/{id}")
    public ResponseEntity<DoctorScheduleDto.DoctorScheduleResponse> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody DoctorScheduleDto.DoctorScheduleRequest request
    ) {
        log.info(
                "REST request to update doctor schedule with id {}",
                id
        );

        DoctorSchedule doctorSchedule =
                toEntity(request);

        DoctorSchedule updatedSchedule =
                doctorScheduleService.update(
                        id,
                        doctorSchedule
                );

        return ResponseEntity.ok(
                toResponse(updatedSchedule)
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

    private DoctorSchedule toEntity(
            DoctorScheduleDto.DoctorScheduleRequest request
    ) {
        Doctor doctor =
                doctorService.findById(request.doctorId());

        DoctorSchedule doctorSchedule =
                new DoctorSchedule();

        doctorSchedule.setDayOfWeek(
                request.dayOfWeek()
        );

        doctorSchedule.setStartTime(
                request.startTime()
        );

        doctorSchedule.setEndTime(
                request.endTime()
        );

        doctorSchedule.setAppointmentDuration(
                request.appointmentDuration()
        );

        doctorSchedule.setDoctor(
                doctor
        );

        return doctorSchedule;
    }

    private DoctorScheduleDto.DoctorScheduleResponse toResponse(
            DoctorSchedule doctorSchedule
    ) {
        Long doctorId = null;
        String doctorFullName = null;

        if (doctorSchedule.getDoctor() != null) {
            doctorId = doctorSchedule.getDoctor().getId();

            doctorFullName =
                    doctorSchedule.getDoctor().getName()
                            + " "
                            + doctorSchedule.getDoctor().getLastName();
        }

        return new DoctorScheduleDto.DoctorScheduleResponse(
                doctorSchedule.getId(),
                doctorSchedule.getDayOfWeek(),
                doctorSchedule.getStartTime(),
                doctorSchedule.getEndTime(),
                doctorSchedule.getAppointmentDuration(),
                doctorId,
                doctorFullName
        );
    }

    private DoctorScheduleDto.DoctorScheduleComboResponse toComboResponse(
            DoctorSchedule doctorSchedule
    ) {
        return new DoctorScheduleDto.DoctorScheduleComboResponse(
                doctorSchedule.getId(),
                doctorSchedule.getDayOfWeek(),
                doctorSchedule.getStartTime(),
                doctorSchedule.getEndTime(),
                doctorSchedule.getAppointmentDuration()
        );
    }
}