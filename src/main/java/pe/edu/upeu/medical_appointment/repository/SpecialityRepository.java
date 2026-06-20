package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.medical_appointment.entity.Speciality;

import java.util.List;
import java.util.Optional;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {

    Optional<Speciality> findByIdAndDeletedFalse(Long id);

    Optional<Speciality> findByNameIgnoreCaseAndDeletedFalse(String name);

    List<Speciality> findByDeletedFalse();

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);
}