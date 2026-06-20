package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.medical_appointment.entity.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByNameAndDeletedFalse(String dni);

    List<Doctor> findByDeletedFalse();

    List<Doctor> findBySpecialityIdAndDeletedFalse(Long specialityId);
}
