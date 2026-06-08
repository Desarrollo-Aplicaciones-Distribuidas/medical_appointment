package pe.edu.upeu.medical_appointment.services;

import pe.edu.upeu.medical_appointment.entity.Doctor;

import java.util.List;

public interface DoctorService {

    Doctor create(Doctor doctor);

    Doctor findByName(String name);

    Doctor update(String name, Doctor doctor );

    Doctor delete(String name);

    List<Doctor> getAll();
}
