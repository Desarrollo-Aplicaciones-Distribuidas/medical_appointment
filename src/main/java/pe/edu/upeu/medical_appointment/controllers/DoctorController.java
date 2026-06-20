package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.dtos.DoctorDto;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.entity.Speciality;
import pe.edu.upeu.medical_appointment.services.DoctorService;
import pe.edu.upeu.medical_appointment.services.SpecialityService;

import java.util.List;

@RestController
@RequestMapping(path = "doctors")
@Tag(name = "Doctor Resources")
public class DoctorController {

    private final Logger log =
            LoggerFactory.getLogger(DoctorController.class);

    private final DoctorService doctorService;
    private final SpecialityService specialityService;

    public DoctorController(
            DoctorService doctorService,
            SpecialityService specialityService
    ) {
        this.doctorService = doctorService;
        this.specialityService = specialityService;
    }

    @Operation(summary = "Get All Doctors")
    @GetMapping
    public ResponseEntity<List<DoctorDto.DoctorResponse>> getAllDoctors() {
        log.info("REST request to get all Doctors");

        List<DoctorDto.DoctorResponse> response =
                doctorService.getAll()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get doctors by speciality")
    @GetMapping("/speciality/{specialityId}")
    public ResponseEntity<List<DoctorDto.DoctorComboResponse>> getDoctorsBySpeciality(
            @PathVariable Long specialityId
    ) {
        log.info(
                "REST request to get doctors by speciality {}",
                specialityId
        );

        List<DoctorDto.DoctorComboResponse> response =
                doctorService.getBySpecialityId(specialityId)
                        .stream()
                        .map(this::toComboResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Save a Doctor")
    @PostMapping
    public ResponseEntity<DoctorDto.DoctorResponse> post(
            @RequestBody DoctorDto.DoctorRequest request
    ) {
        log.info("POST: doctor {}", request.name());

        Doctor doctor = toEntity(request);

        Doctor savedDoctor =
                doctorService.create(doctor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedDoctor));
    }

    @Operation(summary = "Update a Doctor")
    @PutMapping(path = "/{name}")
    public ResponseEntity<DoctorDto.DoctorResponse> put(
            @PathVariable String name,
            @RequestBody DoctorDto.DoctorRequest request
    ) {
        log.info("PUT: doctor {}", name);

        Doctor doctor = toEntity(request);

        Doctor updatedDoctor =
                doctorService.update(name, doctor);

        return ResponseEntity.ok(
                toResponse(updatedDoctor)
        );
    }

    @Operation(summary = "Delete a Doctor")
    @DeleteMapping(path = "/{name}")
    public ResponseEntity<DoctorDto.DoctorResponse> delete(
            @PathVariable String name
    ) {
        log.info("DELETE: doctor {}", name);

        Doctor deletedDoctor =
                doctorService.delete(name);

        return ResponseEntity.ok(
                toResponse(deletedDoctor)
        );
    }

    private Doctor toEntity(
            DoctorDto.DoctorRequest request
    ) {
        Speciality speciality =
                specialityService.findById(request.specialityId());

        Doctor doctor = new Doctor();

        doctor.setName(request.name());
        doctor.setLastName(request.lastName());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());
        doctor.setDeleted(Boolean.FALSE);
        doctor.setSpeciality(speciality);

        return doctor;
    }

    private DoctorDto.DoctorResponse toResponse(
            Doctor doctor
    ) {
        Long specialityId = null;
        String specialityName = null;

        if (doctor.getSpeciality() != null) {
            specialityId = doctor.getSpeciality().getId();
            specialityName = doctor.getSpeciality().getName();
        }

        return new DoctorDto.DoctorResponse(
                doctor.getId(),
                doctor.getName(),
                doctor.getLastName(),
                doctor.getName() + " " + doctor.getLastName(),
                doctor.getEmail(),
                doctor.getPhone(),
                specialityId,
                specialityName
        );
    }

    private DoctorDto.DoctorComboResponse toComboResponse(
            Doctor doctor
    ) {
        return new DoctorDto.DoctorComboResponse(
                doctor.getId(),
                doctor.getName() + " " + doctor.getLastName()
        );
    }
}
