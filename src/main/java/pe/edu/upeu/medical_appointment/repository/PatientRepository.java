package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.medical_appointment.entity.Patient;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient,Long> {
    Optional<Patient> findByDni(String dni);
}
