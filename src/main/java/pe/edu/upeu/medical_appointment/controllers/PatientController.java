package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.entity.Patient;
import pe.edu.upeu.medical_appointment.services.PatientService;

import java.util.List;

@RestController
@RequestMapping(path = "patients")
@Tag(name = "Patient Resources")
public class PatientController {

    private final Logger log = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "Get all patients")
    @GetMapping
    public ResponseEntity<List<Patient>> getAll() {
        log.info("GET: all patients");
        return ResponseEntity.ok(this.patientService.getAll());
    }

    @Operation(summary = "Get Patient by DNI")
    @GetMapping(path = "{dni}")
    public ResponseEntity<Patient> get(@PathVariable String dni) {
        log.info("GET: patient {}", dni);
        return ResponseEntity.ok(this.patientService.findByDni(dni));
    }

    @Operation(summary = "Save a Patient")
    @PostMapping
    public ResponseEntity<Patient> post(
            @RequestBody Patient patient
    ) {
        log.info("POST: patient {}", patient.getName());
        Patient savedPatient = patientService.create(patient);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedPatient);
    }

    @Operation(summary = "Update a Patient")
    @PutMapping(path = "{dni}")
    public ResponseEntity<Patient> put(@PathVariable String dni, @RequestBody Patient patient) {
        log.info("PUT: patient {}", dni);
        return ResponseEntity.ok(this.patientService.update(patient, dni));
    }

    @DeleteMapping(path = "{dni}")
    public ResponseEntity<Patient> delete(@PathVariable String dni) {
        log.info("DELETE: patient {}", dni);
        return ResponseEntity.ok(this.patientService.delete(dni));
    }
}
