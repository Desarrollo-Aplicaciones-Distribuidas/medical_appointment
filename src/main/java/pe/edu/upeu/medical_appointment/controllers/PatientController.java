package pe.edu.upeu.medical_appointment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.medical_appointment.dtos.PatientDto;
import pe.edu.upeu.medical_appointment.entity.Patient;
import pe.edu.upeu.medical_appointment.services.PatientService;

import java.util.List;

@RestController
@RequestMapping(path = "/patients")
@Tag(name = "Patient Resources")
public class PatientController {

    private final Logger log =
            LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(
            PatientService patientService
    ) {
        this.patientService = patientService;
    }

    @Operation(summary = "Get all patients")
    @GetMapping
    public ResponseEntity<List<PatientDto.PatientResponse>> getAll() {
        log.info("GET: all patients");

        List<PatientDto.PatientResponse> response =
                patientService.getAll()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get patient by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto.PatientResponse> getById(
            @PathVariable Long id
    ) {
        log.info("GET: patient by id {}", id);

        Patient patient =
                patientService.findById(id);

        return ResponseEntity.ok(
                toResponse(patient)
        );
    }

    @Operation(summary = "Get patient by DNI")
    @GetMapping("/dni/{dni}")
    public ResponseEntity<PatientDto.PatientResponse> getByDni(
            @PathVariable String dni
    ) {
        log.info("GET: patient by dni {}", dni);

        Patient patient =
                patientService.findByDni(dni);

        return ResponseEntity.ok(
                toResponse(patient)
        );
    }

    @Operation(summary = "Search patients by name, last name or DNI")
    @GetMapping("/search")
    public ResponseEntity<List<PatientDto.PatientComboResponse>> search(
            @RequestParam String term
    ) {
        log.info("GET: search patients by term {}", term);

        List<PatientDto.PatientComboResponse> response =
                patientService.search(term)
                        .stream()
                        .map(this::toComboResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Save a Patient")
    @PostMapping
    public ResponseEntity<PatientDto.PatientResponse> post(
            @Valid @RequestBody PatientDto.PatientRequest request
    ) {
        log.info("POST: patient {}", request.name());

        Patient patient =
                toEntity(request);

        Patient savedPatient =
                patientService.create(patient);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedPatient));
    }

    @Operation(summary = "Update a Patient")
    @PutMapping("/{id}")
    public ResponseEntity<PatientDto.PatientResponse> put(
            @PathVariable Long id,
            @Valid @RequestBody PatientDto.PatientRequest request
    ) {
        log.info("PUT: patient with id {}", id);

        Patient patient =
                toEntity(request);

        Patient updatedPatient =
                patientService.update(
                        id,
                        patient
                );

        return ResponseEntity.ok(
                toResponse(updatedPatient)
        );
    }

    @Operation(summary = "Delete a Patient")
    @DeleteMapping("/{id}")
    public ResponseEntity<PatientDto.PatientResponse> delete(
            @PathVariable Long id
    ) {
        log.info("DELETE: patient with id {}", id);

        Patient deletedPatient =
                patientService.delete(id);

        return ResponseEntity.ok(
                toResponse(deletedPatient)
        );
    }

    private Patient toEntity(
            PatientDto.PatientRequest request
    ) {
        Patient patient = new Patient();

        patient.setName(
                request.name()
        );

        patient.setLastName(
                request.lastName()
        );

        patient.setDni(
                request.dni()
        );

        patient.setEmail(
                request.email()
        );

        patient.setPhone(
                request.phone()
        );

        patient.setDeleted(
                Boolean.FALSE
        );

        return patient;
    }

    private PatientDto.PatientResponse toResponse(
            Patient patient
    ) {
        return new PatientDto.PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getLastName(),
                patient.getName() + " " + patient.getLastName(),
                patient.getDni(),
                patient.getEmail(),
                patient.getPhone()
        );
    }

    private PatientDto.PatientComboResponse toComboResponse(
            Patient patient
    ) {
        return new PatientDto.PatientComboResponse(
                patient.getId(),
                patient.getName() + " " + patient.getLastName(),
                patient.getDni()
        );
    }
}