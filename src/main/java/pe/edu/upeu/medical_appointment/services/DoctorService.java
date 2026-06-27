package pe.edu.upeu.medical_appointment.services;

import pe.edu.upeu.medical_appointment.entity.Doctor;

import java.util.List;

public interface DoctorService {

    List<Doctor> getAll();

    List<Doctor> getBySpecialityId(Long specialityId);

    Doctor findById(Long id);

    Doctor findByEmail(String email);

    Doctor create(Doctor doctor);

    Doctor update(Long id, Doctor doctor);

    Doctor delete(Long id);
}