package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.medical_appointment.entity.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByDeletedFalse();

    Optional<Doctor> findByIdAndDeletedFalse(Long id);

    Optional<Doctor> findByEmailIgnoreCaseAndDeletedFalse(String email);

    Optional<Doctor> findByEmailIgnoreCaseAndDeletedTrue(String email);

    boolean existsByEmailIgnoreCaseAndDeletedFalse(String email);

    List<Doctor> findBySpecialityIdAndDeletedFalse(Long specialityId);
}