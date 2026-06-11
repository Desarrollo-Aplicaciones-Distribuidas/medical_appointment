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

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAll() {
        return this.patientRepository.findByDeletedFalse();
    }

    @Override
    public Patient create(Patient patient) {
        patient.setDeleted(Boolean.FALSE);
        return this.patientRepository.save(patient);
    }

    @Override
    public Patient findByDni(String dni) {
        return this.patientRepository.findByDniAndDeletedFalse(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with dni " + dni + " does not exists"));
    }

    @Override
    public Patient update(Patient patient, String dni) {

        var patientToUpdate = this.findByDni(dni);

        patientToUpdate.setName(patient.getName());
        patientToUpdate.setLastName(patient.getLastName());
        patientToUpdate.setDni(patient.getDni());
        patientToUpdate.setEmail(patient.getEmail());
        patientToUpdate.setPhone(patient.getPhone());

        return patientRepository.save(patientToUpdate);
    }

    @Override
    public Patient delete(String dni) {
        var patientToDelete = this.findByDni(dni);
        patientToDelete.setDeleted(true);
        return this.patientRepository.save(patientToDelete);
    }

}
