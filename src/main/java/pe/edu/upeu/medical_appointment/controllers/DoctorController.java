package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.services.DoctorService;

import java.util.List;

@RestController
@RequestMapping(path = "doctors")
@Tag(name = "Doctor Resources")
public class DoctorController {

    private final Logger log = LoggerFactory.getLogger(DoctorController.class);
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Operation(summary = "Get All Doctors")
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        log.info("REST request to get all Doctors");
        return ResponseEntity.ok(this.doctorService.getAll());
    }

    @Operation(summary = "Save a Doctor")
    @PostMapping
    public ResponseEntity<Doctor> post(
            @RequestBody Doctor doctor
    ) {
        log.info("POST: doctor {}", doctor.getName());
        Doctor savedDoctor = doctorService.create(doctor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedDoctor);
    }

    @Operation(summary = "Update a Doctor")
    @PutMapping(path = "{name}")
    public ResponseEntity<Doctor> put(@PathVariable String name, @RequestBody Doctor doctor) {
        log.info("PUT: doctor {}", name);
        return ResponseEntity.ok(this.doctorService.update(name, doctor));
    }

    @DeleteMapping(path = "{name}")
    public ResponseEntity<Doctor> delete(@PathVariable String name) {
        log.info("DELETE: doctor {}", name);
        return ResponseEntity.ok(this.doctorService.delete(name));
    }
}
