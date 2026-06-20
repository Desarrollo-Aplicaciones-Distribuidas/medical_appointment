package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.entity.Patient;
import pe.edu.upeu.medical_appointment.exceptions.ResourceNotFoundException;
import pe.edu.upeu.medical_appointment.repository.PatientRepository;

import java.util.List;

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
        return patientRepository.findByDeletedFalse();
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
            return patientRepository.findByDeletedFalse();
        }

        return patientRepository.searchActivePatients(
                term.trim()
        );
    }

    @Override
    public Patient create(
            Patient patient
    ) {
        validatePatient(patient);

        if (patientRepository.existsByDniAndDeletedFalse(
                patient.getDni()
        )) {
            throw new IllegalArgumentException(
                    "A patient with dni " + patient.getDni() + " already exists"
            );
        }

        if (patient.getEmail() != null &&
                !patient.getEmail().trim().isEmpty() &&
                patientRepository.existsByEmailAndDeletedFalse(
                        patient.getEmail()
                )) {
            throw new IllegalArgumentException(
                    "A patient with email " + patient.getEmail() + " already exists"
            );
        }

        patient.setDeleted(Boolean.FALSE);

        return patientRepository.save(patient);
    }

    @Override
    public Patient update(
            Long id,
            Patient patient
    ) {
        Patient existingPatient = findById(id);

        validatePatient(patient);

        if (!existingPatient.getDni().equals(patient.getDni()) &&
                patientRepository.existsByDniAndDeletedFalse(
                        patient.getDni()
                )) {
            throw new IllegalArgumentException(
                    "A patient with dni " + patient.getDni() + " already exists"
            );
        }

        if (patient.getEmail() != null &&
                !patient.getEmail().trim().isEmpty() &&
                existingPatient.getEmail() != null &&
                !existingPatient.getEmail().equals(patient.getEmail()) &&
                patientRepository.existsByEmailAndDeletedFalse(
                        patient.getEmail()
                )) {
            throw new IllegalArgumentException(
                    "A patient with email " + patient.getEmail() + " already exists"
            );
        }

        if (patient.getEmail() != null &&
                !patient.getEmail().trim().isEmpty() &&
                existingPatient.getEmail() == null &&
                patientRepository.existsByEmailAndDeletedFalse(
                        patient.getEmail()
                )) {
            throw new IllegalArgumentException(
                    "A patient with email " + patient.getEmail() + " already exists"
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
        Patient patientToDelete = findById(id);

        patientToDelete.setDeleted(Boolean.TRUE);

        return patientRepository.save(patientToDelete);
    }

    private void validatePatient(
            Patient patient
    ) {
        if (patient == null) {
            throw new IllegalArgumentException(
                    "Patient is required"
            );
        }

        if (patient.getName() == null ||
                patient.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Patient name is required"
            );
        }

        if (patient.getLastName() == null ||
                patient.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Patient last name is required"
            );
        }

        if (patient.getDni() == null ||
                patient.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Patient dni is required"
            );
        }

        if (patient.getDni().length() != 8) {
            throw new IllegalArgumentException(
                    "Patient dni must have 8 digits"
            );
        }

        if (!patient.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "Patient dni must contain only numbers"
            );
        }
    }
}