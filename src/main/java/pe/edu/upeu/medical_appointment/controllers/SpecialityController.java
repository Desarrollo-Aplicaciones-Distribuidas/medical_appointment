package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.dtos.SpecialityDto;
import pe.edu.upeu.medical_appointment.entity.Speciality;
import pe.edu.upeu.medical_appointment.services.SpecialityService;

import java.util.List;

@RestController
@RequestMapping(path = "/specialities")
public class SpecialityController {

    private static final Logger log =
            LoggerFactory.getLogger(SpecialityController.class);

    private final SpecialityService specialityService;

    public SpecialityController(
            SpecialityService specialityService
    ) {
        this.specialityService = specialityService;
    }

    @Operation(summary = "Get all Specialities")
    @GetMapping
    public ResponseEntity<List<SpecialityDto.SpecialityResponse>> getAll() {
        log.info("GET: all specialities");

        List<SpecialityDto.SpecialityResponse> response =
                specialityService.getAll()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Specialities for combo")
    @GetMapping("/combo")
    public ResponseEntity<List<SpecialityDto.SpecialityComboResponse>> getAllForCombo() {
        log.info("GET: all specialities for combo");

        List<SpecialityDto.SpecialityComboResponse> response =
                specialityService.getAll()
                        .stream()
                        .map(this::toComboResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Speciality by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SpecialityDto.SpecialityResponse> findById(
            @PathVariable Long id
    ) {
        log.info("GET: speciality with id {}", id);

        Speciality speciality =
                specialityService.findById(id);

        return ResponseEntity.ok(
                toResponse(speciality)
        );
    }

    @Operation(summary = "Get Speciality by name")
    @GetMapping("/name/{name}")
    public ResponseEntity<SpecialityDto.SpecialityResponse> findByName(
            @PathVariable String name
    ) {
        log.info("GET: speciality with name {}", name);

        Speciality speciality =
                specialityService.findByName(name);

        return ResponseEntity.ok(
                toResponse(speciality)
        );
    }

    @Operation(summary = "Save a Speciality")
    @PostMapping
    public ResponseEntity<SpecialityDto.SpecialityResponse> post(
            @Valid @RequestBody SpecialityDto.SpecialityRequest request
    ) {
        log.info("POST: speciality {}", request.name());

        Speciality speciality =
                toEntity(request);

        Speciality savedSpeciality =
                specialityService.create(speciality);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedSpeciality));
    }

    @Operation(summary = "Update a Speciality by ID")
    @PutMapping("/{id}")
    public ResponseEntity<SpecialityDto.SpecialityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SpecialityDto.SpecialityRequest request
    ) {
        log.info("PUT: speciality with id {}", id);

        Speciality speciality =
                toEntity(request);

        Speciality updatedSpeciality =
                specialityService.update(
                        id,
                        speciality
                );

        return ResponseEntity.ok(
                toResponse(updatedSpeciality)
        );
    }

    @Operation(summary = "Delete a Speciality by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<SpecialityDto.SpecialityResponse> delete(
            @PathVariable Long id
    ) {
        log.info("DELETE: speciality with id {}", id);

        Speciality deletedSpeciality =
                specialityService.delete(id);

        return ResponseEntity.ok(
                toResponse(deletedSpeciality)
        );
    }

    private Speciality toEntity(
            SpecialityDto.SpecialityRequest request
    ) {
        Speciality speciality = new Speciality();

        speciality.setName(
                request.name()
        );

        speciality.setDescription(
                request.description()
        );

        speciality.setDeleted(
                Boolean.FALSE
        );

        return speciality;
    }

    private SpecialityDto.SpecialityResponse toResponse(
            Speciality speciality
    ) {
        return new SpecialityDto.SpecialityResponse(
                speciality.getId(),
                speciality.getName(),
                speciality.getDescription()
        );
    }

    private SpecialityDto.SpecialityComboResponse toComboResponse(
            Speciality speciality
    ) {
        return new SpecialityDto.SpecialityComboResponse(
                speciality.getId(),
                speciality.getName()
        );
    }
}