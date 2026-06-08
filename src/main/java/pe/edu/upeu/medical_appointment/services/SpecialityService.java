package pe.edu.upeu.medical_appointment.services;

import pe.edu.upeu.medical_appointment.entity.Speciality;

import java.util.List;

public interface SpecialityService {

    Speciality create(Speciality speciality);

    Speciality findById(Long id);

    Speciality update(Long id, Speciality speciality);

    Speciality delete(Long id);

    List<Speciality> getAll();
}
