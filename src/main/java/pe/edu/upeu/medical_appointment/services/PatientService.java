package pe.edu.upeu.medical_appointment.services;

import pe.edu.upeu.medical_appointment.entity.Patient;

import java.util.List;

public interface PatientService {

    Patient create(Patient patient);
    Patient readByDni(String dni);
    Patient update(Patient patient, String dni);
    void delete(String dni);

    List<Patient> getAll();
}
