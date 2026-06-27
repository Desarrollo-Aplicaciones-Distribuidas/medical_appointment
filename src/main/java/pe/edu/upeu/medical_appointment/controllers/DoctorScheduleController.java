package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctor-schedules")
@Tag(name = "Doctor Schedule Resources")
public class DoctorScheduleController {

    private static final Logger log = LoggerFactory.getLogger(DoctorScheduleController.class);

    private final DoctorScheduleService doctorScheduleService;

    public DoctorScheduleController(DoctorScheduleService doctorScheduleService) {
        this.doctorScheduleService = doctorScheduleService;
    }

    @Operation(summary = "Get all doctor schedules")
    @GetMapping
    public ResponseEntity<List<DoctorScheduleDto.DoctorScheduleResponse>> getAll() {
        log.info("GET: all doctor schedules");

        List<DoctorScheduleDto.DoctorScheduleResponse> response = doctorScheduleService.getAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get doctor schedules grouped by doctor")
    @GetMapping("/grouped-by-doctor")
    public ResponseEntity<List<DoctorScheduleDto.DoctorScheduleGroupedByDoctorResponse>> getGroupedByDoctor() {
        log.info("GET: doctor schedules grouped by doctor");

        List<DoctorScheduleDto.DoctorScheduleGroupedByDoctorResponse> response = doctorScheduleService.getAll()
                .stream()
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getDoctor().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .values()
                .stream()
                .map(this::toGroupedResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get doctor schedule by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DoctorScheduleDto.DoctorScheduleResponse> findById(@PathVariable Long id) {
        log.info("GET: doctor schedule with id {}", id);

        DoctorSchedule doctorSchedule = doctorScheduleService.findById(id);

        return ResponseEntity.ok(toResponse(doctorSchedule));
    }

    @Operation(summary = "Get doctor schedules by doctor ID")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorScheduleDto.DoctorScheduleResponse>> getByDoctorId(
            @PathVariable Long doctorId
    ) {
        log.info("GET: doctor schedules by doctor id {}", doctorId);

        List<DoctorScheduleDto.DoctorScheduleResponse> response = doctorScheduleService
                .getByDoctorId(doctorId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get doctor schedules by doctor ID and day of week")
    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    public ResponseEntity<List<DoctorScheduleDto.DoctorScheduleResponse>> getByDoctorIdAndDayOfWeek(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek
    ) {
        log.info("GET: doctor schedules by doctor id {} and day {}", doctorId, dayOfWeek);

        List<DoctorScheduleDto.DoctorScheduleResponse> response = doctorScheduleService
                .getByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get doctor schedules for combo by doctor ID and day of week")
    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}/combo")
    public ResponseEntity<List<DoctorScheduleDto.DoctorScheduleComboResponse>> getComboByDoctorIdAndDayOfWeek(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek
    ) {
        log.info("GET: doctor schedule combo by doctor id {} and day {}", doctorId, dayOfWeek);

        List<DoctorScheduleDto.DoctorScheduleComboResponse> response = doctorScheduleService
                .getByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .stream()
                .map(this::toComboResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Save a doctor schedule")
    @PostMapping
    public ResponseEntity<DoctorScheduleDto.DoctorScheduleResponse> post(
            @Valid @RequestBody DoctorScheduleDto.DoctorScheduleRequest request
    ) {
        log.info("POST: doctor schedule for doctor id {}", request.doctorId());

        DoctorSchedule doctorSchedule = toEntity(request);

        DoctorSchedule savedDoctorSchedule = doctorScheduleService.create(doctorSchedule);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedDoctorSchedule));
    }

    @Operation(summary = "Save multiple doctor schedules")
    @PostMapping("/bulk")
    public ResponseEntity<DoctorScheduleDto.DoctorScheduleBulkResponse> postBulk(
            @Valid @RequestBody DoctorScheduleDto.DoctorScheduleBulkRequest request
    ) {
        log.info("POST: bulk doctor schedules for doctor id {}", request.doctorId());

        List<DoctorScheduleDto.DoctorScheduleResponse> created = doctorScheduleService
                .createBulk(request)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new DoctorScheduleDto.DoctorScheduleBulkResponse(created));
    }

    @Operation(summary = "Update a doctor schedule by ID")
    @PutMapping("/{id}")
    public ResponseEntity<DoctorScheduleDto.DoctorScheduleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DoctorScheduleDto.DoctorScheduleRequest request
    ) {
        log.info("PUT: doctor schedule with id {}", id);

        DoctorSchedule doctorSchedule = toEntity(request);

        DoctorSchedule updatedDoctorSchedule = doctorScheduleService.update(id, doctorSchedule);

        return ResponseEntity.ok(toResponse(updatedDoctorSchedule));
    }

    @Operation(summary = "Delete a doctor schedule by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<DoctorScheduleDto.DoctorScheduleResponse> delete(@PathVariable Long id) {
        log.info("DELETE: doctor schedule with id {}", id);

        DoctorSchedule deletedDoctorSchedule = doctorScheduleService.delete(id);

        return ResponseEntity.ok(toResponse(deletedDoctorSchedule));
    }

    private DoctorSchedule toEntity(DoctorScheduleDto.DoctorScheduleRequest request) {
        Doctor doctor = new Doctor();
        doctor.setId(request.doctorId());

        DoctorSchedule doctorSchedule = new DoctorSchedule();

        doctorSchedule.setDayOfWeek(request.dayOfWeek());
        doctorSchedule.setStartTime(request.startTime());
        doctorSchedule.setEndTime(request.endTime());
        doctorSchedule.setAppointmentDuration(request.appointmentDuration());
        doctorSchedule.setDoctor(doctor);
        doctorSchedule.setDeleted(Boolean.FALSE);

        return doctorSchedule;
    }

    private DoctorScheduleDto.DoctorScheduleResponse toResponse(DoctorSchedule doctorSchedule) {
        Long doctorId = null;
        String doctorFullName = null;

        if (doctorSchedule.getDoctor() != null) {
            doctorId = doctorSchedule.getDoctor().getId();

            doctorFullName = doctorSchedule.getDoctor().getName()
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

    private DoctorScheduleDto.DoctorScheduleGroupedByDoctorResponse toGroupedResponse(
            List<DoctorSchedule> schedules
    ) {
        DoctorSchedule first = schedules.get(0);

        String doctorFullName = first.getDoctor().getName()
                + " "
                + first.getDoctor().getLastName();

        return new DoctorScheduleDto.DoctorScheduleGroupedByDoctorResponse(
                first.getDoctor().getId(),
                doctorFullName,
                schedules.stream().map(this::toResponse).toList()
        );
    }

}