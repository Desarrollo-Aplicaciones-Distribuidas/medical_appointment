package pe.edu.upeu.medical_appointment.services;

import pe.edu.upeu.medical_appointment.entity.Speciality;

import java.util.List;

public interface SpecialityService {

    List<Speciality> getAll();

    Speciality findById(Long id);

    Speciality findByName(String name);

    Speciality create(Speciality speciality);

    Speciality update(Long id, Speciality speciality);

    Speciality delete(Long id);
}