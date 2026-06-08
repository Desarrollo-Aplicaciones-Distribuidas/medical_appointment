package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.entity.Speciality;
import pe.edu.upeu.medical_appointment.services.SpecialityService;

import java.util.List;

@RestController
@RequestMapping(path = "specialities")
public class SpecialityController {

    private final SpecialityService specialityService;
    private final Logger log = LoggerFactory.getLogger(SpecialityController.class);

    public SpecialityController(SpecialityService specialityService) {
        this.specialityService = specialityService;
    }

    @Operation(summary = "Get all Specialities")
    @GetMapping
    public ResponseEntity<List<Speciality>> getAll() {
        log.info("GET: all specialities");
        return ResponseEntity.ok(this.specialityService.getAll());
    }

    @Operation(summary = "Get Speciality by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Speciality> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                specialityService.findById(id)
        );
    }

    @Operation(summary = "Save a Speciality")
    @PostMapping
    public ResponseEntity<Speciality> post(@RequestBody Speciality speciality) {
        log.info("POST: speciality {}", speciality);
        Speciality savedSpeciality = specialityService.create(speciality);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedSpeciality);
    }

    @Operation(summary = "Update an Speciality by ID")
    @PutMapping("/{id}")
    public ResponseEntity<Speciality> update(
            @PathVariable Long id,
            @RequestBody Speciality speciality
    ) {

        return ResponseEntity.ok(
                specialityService.update(id, speciality)
        );
    }

    @Operation(summary = "Delete an Speciality by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Speciality> delete(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                specialityService.delete(id)
        );
    }
}
