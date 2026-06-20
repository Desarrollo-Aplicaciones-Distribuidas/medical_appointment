package pe.edu.upeu.medical_appointment.services;

import pe.edu.upeu.medical_appointment.entity.Patient;

import java.util.List;

public interface PatientService {

    Patient create(Patient patient);

    Patient findById(Long id);

    Patient findByDni(String dni);

    List<Patient> search(String term);

    Patient update(Long id, Patient patient);

    Patient delete(Long id);

    List<Patient> getAll();
}