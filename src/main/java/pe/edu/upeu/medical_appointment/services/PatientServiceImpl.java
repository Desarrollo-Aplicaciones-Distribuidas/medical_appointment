package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.entity.Patient;
import pe.edu.upeu.medical_appointment.repository.PatientRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAll() {
        return this.patientRepository.findAll();
    }

    @Override
    public Patient create(Patient patient) {
        return this.patientRepository.save(patient);
    }

    @Override
    public Patient readByDni(String dni) {
        return this.patientRepository.findByDni(dni)
                .orElseThrow(() -> new NoSuchElementException("Patient with dni " + dni + " does not exist"));
    }

    @Override
    public Patient update(Patient patient, String dni) {
        var patientToUpdate = this.readByDni(dni);
        patientToUpdate.setName(patient.getName());
        patientToUpdate.setLastName(patient.getLastName());
        patientToUpdate.setDni(patient.getDni());
        patientToUpdate.setEmail(patient.getEmail());
        patientToUpdate.setPhone(patient.getPhone());
        patientRepository.save(patientToUpdate);
        return null;
    }

    @Override
    public void delete(String dni) {
        var patientToDelete = this.readByDni(dni);
        this.patientRepository.delete(patientToDelete);
    }


}
