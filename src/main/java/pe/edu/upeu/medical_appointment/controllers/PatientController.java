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

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "Get all active patients")
    @GetMapping
    public ResponseEntity<List<PatientDto.PatientResponse>> getAll() {
        log.info("GET: all active patients");

        List<PatientDto.PatientResponse> response = patientService.getAll().stream().map(this::toResponse).toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get active patient by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto.PatientResponse> getById(@PathVariable Long id) {
        log.info("GET: active patient by id {}", id);

        Patient patient = patientService.findById(id);

        return ResponseEntity.ok(toResponse(patient));
    }

    @Operation(summary = "Get active patient by DNI")
    @GetMapping("/dni/{dni}")
    public ResponseEntity<PatientDto.PatientResponse> getByDni(@PathVariable String dni) {
        log.info("GET: active patient by dni {}", dni);

        Patient patient = patientService.findByDni(dni);

        return ResponseEntity.ok(toResponse(patient));
    }

    @Operation(summary = "Search active patients by name, last name, DNI or email")
    @GetMapping("/search")
    public ResponseEntity<List<PatientDto.PatientComboResponse>> search(@RequestParam(required = false) String term) {
        log.info("GET: search active patients by term {}", term);

        List<PatientDto.PatientComboResponse> response = patientService.search(term).stream().map(this::toComboResponse).toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a patient")
    @PostMapping
    public ResponseEntity<PatientDto.PatientResponse> post(@Valid @RequestBody PatientDto.PatientRequest request) {
        log.info("POST: create patient with dni {}", request.dni());

        Patient patient = toEntity(request);

        Patient savedPatient = patientService.create(patient);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedPatient));
    }

    @Operation(summary = "Update an active patient")
    @PutMapping("/{id}")
    public ResponseEntity<PatientDto.PatientResponse> put(@PathVariable Long id, @Valid @RequestBody PatientDto.PatientRequest request) {
        log.info("PUT: update patient with id {}", id);

        Patient patient = toEntity(request);

        Patient updatedPatient = patientService.update(id, patient);

        return ResponseEntity.ok(toResponse(updatedPatient));
    }

    @Operation(summary = "Soft delete a patient")
    @DeleteMapping("/{id}")
    public ResponseEntity<PatientDto.PatientResponse> delete(@PathVariable Long id) {
        log.info("DELETE: soft delete patient with id {}", id);

        Patient deletedPatient = patientService.delete(id);

        return ResponseEntity.ok(toResponse(deletedPatient));
    }

    @Operation(summary = "Restore a deleted patient")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<PatientDto.PatientResponse> restore(@PathVariable Long id) {
        log.info("PATCH: restore patient with id {}", id);

        Patient restoredPatient = patientService.restore(id);

        return ResponseEntity.ok(toResponse(restoredPatient));
    }

    private Patient toEntity(PatientDto.PatientRequest request) {
        Patient patient = new Patient();

        patient.setName(request.name());

        patient.setLastName(request.lastName());

        patient.setDni(request.dni());

        patient.setEmail(request.email());

        patient.setPhone(request.phone());

        patient.setDeleted(Boolean.FALSE);

        return patient;
    }

    private PatientDto.PatientResponse toResponse(Patient patient) {
        return new PatientDto.PatientResponse(patient.getId(), patient.getName(), patient.getLastName(), buildFullName(patient.getName(), patient.getLastName()), patient.getDni(), patient.getEmail(), patient.getPhone());
    }

    private PatientDto.PatientComboResponse toComboResponse(Patient patient) {
        return new PatientDto.PatientComboResponse(patient.getId(), buildFullName(patient.getName(), patient.getLastName()), patient.getDni());
    }

    private String buildFullName(String name, String lastName) {
        String safeName = name != null ? name.trim() : "";

        String safeLastName = lastName != null ? lastName.trim() : "";

        String fullName = (safeName + " " + safeLastName).trim();

        return fullName.isEmpty() ? null : fullName;
    }
}