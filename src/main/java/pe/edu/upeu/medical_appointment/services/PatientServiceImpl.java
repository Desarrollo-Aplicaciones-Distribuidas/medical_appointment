package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.entity.Patient;
import pe.edu.upeu.medical_appointment.exceptions.ResourceNotFoundException;
import pe.edu.upeu.medical_appointment.repository.PatientRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(
            PatientRepository patientRepository
    ) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAll() {
        return patientRepository.findByDeletedFalse(
                getDefaultSort()
        );
    }

    @Override
    public Patient findById(
            Long id
    ) {
        return patientRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: " + id
                        )
                );
    }

    @Override
    public Patient findByDni(
            String dni
    ) {
        return patientRepository.findByDniAndDeletedFalse(dni)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with dni: " + dni
                        )
                );
    }

    @Override
    public List<Patient> search(
            String term
    ) {
        if (term == null || term.trim().isEmpty()) {
            return patientRepository.findByDeletedFalse(
                    getDefaultSort()
            );
        }

        return patientRepository.searchActivePatients(
                term.trim(),
                getDefaultSort()
        );
    }

    @Override
    public Patient create(
            Patient patient
    ) {
        validatePatient(patient);
        normalizePatient(patient);

        if (patientRepository.existsByDniAndDeletedFalse(
                patient.getDni()
        )) {
            throw new IllegalArgumentException(
                    "Ya existe un paciente activo con el DNI " + patient.getDni()
            );
        }

        Optional<Patient> deletedPatient =
                patientRepository.findByDniAndDeletedTrue(
                        patient.getDni()
                );

        if (deletedPatient.isPresent()) {
            Patient existingPatient =
                    deletedPatient.get();

            existingPatient.setName(
                    patient.getName()
            );

            existingPatient.setLastName(
                    patient.getLastName()
            );

            existingPatient.setEmail(
                    patient.getEmail()
            );

            existingPatient.setPhone(
                    patient.getPhone()
            );

            existingPatient.setDeleted(Boolean.FALSE);

            return patientRepository.save(existingPatient);
        }

        patient.setDeleted(Boolean.FALSE);

        return patientRepository.save(patient);
    }

    @Override
    public Patient update(
            Long id,
            Patient patient
    ) {
        Patient existingPatient =
                findById(id);

        validatePatient(patient);
        normalizePatient(patient);

        if (patientRepository.existsByDniAndDeletedFalseAndIdNot(
                patient.getDni(),
                id
        )) {
            throw new IllegalArgumentException(
                    "Ya existe un paciente activo con el DNI " + patient.getDni()
            );
        }

        existingPatient.setName(
                patient.getName()
        );

        existingPatient.setLastName(
                patient.getLastName()
        );

        existingPatient.setDni(
                patient.getDni()
        );

        existingPatient.setEmail(
                patient.getEmail()
        );

        existingPatient.setPhone(
                patient.getPhone()
        );

        return patientRepository.save(existingPatient);
    }

    @Override
    public Patient delete(
            Long id
    ) {
        Patient patientToDelete =
                findById(id);

        patientToDelete.setDeleted(Boolean.TRUE);

        return patientRepository.save(patientToDelete);
    }

    @Override
    public Patient restore(
            Long id
    ) {
        Patient patientToRestore =
                patientRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with id: " + id
                                )
                        );

        if (Boolean.FALSE.equals(patientToRestore.getDeleted())) {
            return patientToRestore;
        }

        if (patientRepository.existsByDniAndDeletedFalse(
                patientToRestore.getDni()
        )) {
            throw new IllegalArgumentException(
                    "No se puede restaurar el paciente porque ya existe un paciente activo con el DNI "
                            + patientToRestore.getDni()
            );
        }

        patientToRestore.setDeleted(Boolean.FALSE);

        return patientRepository.save(patientToRestore);
    }

    private void validatePatient(
            Patient patient
    ) {
        if (patient == null) {
            throw new IllegalArgumentException(
                    "Patient is required"
            );
        }

        if (!hasText(patient.getName())) {
            throw new IllegalArgumentException(
                    "Patient name is required"
            );
        }

        if (!hasText(patient.getLastName())) {
            throw new IllegalArgumentException(
                    "Patient last name is required"
            );
        }

        if (!hasText(patient.getDni())) {
            throw new IllegalArgumentException(
                    "Patient dni is required"
            );
        }

        String dni =
                patient.getDni().trim();

        if (dni.length() != 8) {
            throw new IllegalArgumentException(
                    "Patient dni must have 8 digits"
            );
        }

        if (!dni.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "Patient dni must contain only numbers"
            );
        }
    }

    private void normalizePatient(
            Patient patient
    ) {
        patient.setName(
                patient.getName().trim()
        );

        patient.setLastName(
                patient.getLastName().trim()
        );

        patient.setDni(
                patient.getDni().trim()
        );

        if (hasText(patient.getEmail())) {
            patient.setEmail(
                    patient.getEmail().trim().toLowerCase()
            );
        } else {
            patient.setEmail(null);
        }

        if (hasText(patient.getPhone())) {
            patient.setPhone(
                    patient.getPhone().trim()
            );
        } else {
            patient.setPhone(null);
        }
    }

    private Sort getDefaultSort() {
        return Sort.by(
                Sort.Order.asc("lastName"),
                Sort.Order.asc("name")
        );
    }

    private boolean hasText(
            String value
    ) {
        return value != null &&
                !value.trim().isEmpty();
    }
}