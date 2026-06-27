package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.dtos.DoctorDto;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.entity.Speciality;
import pe.edu.upeu.medical_appointment.services.DoctorService;

import java.util.List;

@RestController
@RequestMapping(path = "/doctors")
@Tag(name = "Doctor Resources")
public class DoctorController {

    private static final Logger log = LoggerFactory.getLogger(DoctorController.class);

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Operation(summary = "Get all Doctors")
    @GetMapping
    public ResponseEntity<List<DoctorDto.DoctorResponse>> getAll() {
        log.info("GET: all doctors");

        List<DoctorDto.DoctorResponse> response = doctorService.getAll().stream().map(this::toResponse).toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Doctors for combo")
    @GetMapping("/combo")
    public ResponseEntity<List<DoctorDto.DoctorComboResponse>> getAllForCombo() {
        log.info("GET: all doctors for combo");

        List<DoctorDto.DoctorComboResponse> response = doctorService.getAll().stream().map(this::toComboResponse).toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Doctors by Speciality")
    @GetMapping("/speciality/{specialityId}")
    public ResponseEntity<List<DoctorDto.DoctorComboResponse>> getBySpecialityId(@PathVariable Long specialityId) {
        log.info("GET: doctors by speciality id {}", specialityId);

        List<DoctorDto.DoctorComboResponse> response = doctorService.getBySpecialityId(specialityId).stream().map(this::toComboResponse).toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Doctor by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto.DoctorResponse> findById(@PathVariable Long id) {
        log.info("GET: doctor with id {}", id);

        Doctor doctor = doctorService.findById(id);

        return ResponseEntity.ok(toResponse(doctor));
    }

    @Operation(summary = "Get Doctor by email")
    @GetMapping("/email/{email}")
    public ResponseEntity<DoctorDto.DoctorResponse> findByEmail(@PathVariable String email) {
        log.info("GET: doctor with email {}", email);

        Doctor doctor = doctorService.findByEmail(email);

        return ResponseEntity.ok(toResponse(doctor));
    }

    @Operation(summary = "Save a Doctor")
    @PostMapping
    public ResponseEntity<DoctorDto.DoctorResponse> post(@Valid @RequestBody DoctorDto.DoctorRequest request) {
        log.info("POST: doctor {}", request.email());

        Doctor doctor = toEntity(request);

        Doctor savedDoctor = doctorService.create(doctor);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedDoctor));
    }

    @Operation(summary = "Update a Doctor by ID")
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDto.DoctorResponse> update(@PathVariable Long id, @Valid @RequestBody DoctorDto.DoctorRequest request) {
        log.info("PUT: doctor with id {}", id);

        Doctor doctor = toEntity(request);

        Doctor updatedDoctor = doctorService.update(id, doctor);

        return ResponseEntity.ok(toResponse(updatedDoctor));
    }

    @Operation(summary = "Delete a Doctor by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<DoctorDto.DoctorResponse> delete(@PathVariable Long id) {
        log.info("DELETE: doctor with id {}", id);

        Doctor deletedDoctor = doctorService.delete(id);

        return ResponseEntity.ok(toResponse(deletedDoctor));
    }

    private Doctor toEntity(DoctorDto.DoctorRequest request) {
        Speciality speciality = new Speciality();
        speciality.setId(request.specialityId());

        Doctor doctor = new Doctor();

        doctor.setName(request.name());
        doctor.setLastName(request.lastName());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());
        doctor.setSpeciality(speciality);
        doctor.setDeleted(Boolean.FALSE);

        return doctor;
    }

    private DoctorDto.DoctorResponse toResponse(Doctor doctor) {
        Long specialityId = null;
        String specialityName = null;

        if (doctor.getSpeciality() != null) {
            specialityId = doctor.getSpeciality().getId();
            specialityName = doctor.getSpeciality().getName();
        }

        return new DoctorDto.DoctorResponse(doctor.getId(), doctor.getName(), doctor.getLastName(), doctor.getName() + " " + doctor.getLastName(), doctor.getEmail(), doctor.getPhone(), specialityId, specialityName);
    }

    private DoctorDto.DoctorComboResponse toComboResponse(Doctor doctor) {
        return new DoctorDto.DoctorComboResponse(doctor.getId(), doctor.getName() + " " + doctor.getLastName());
    }
}