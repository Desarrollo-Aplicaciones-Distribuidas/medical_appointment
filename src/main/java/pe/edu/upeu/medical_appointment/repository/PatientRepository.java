package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upeu.medical_appointment.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByDniAndDeletedFalse(String dni);

    Optional<Patient> findByIdAndDeletedFalse(Long id);

    List<Patient> findByDeletedFalse();

    boolean existsByDniAndDeletedFalse(String dni);

    boolean existsByEmailAndDeletedFalse(String email);

    @Query("""
            SELECT p
            FROM Patient p
            WHERE p.deleted = false
              AND (
                    LOWER(p.name) LIKE LOWER(CONCAT('%', :term, '%'))
                 OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :term, '%'))
                 OR p.dni LIKE CONCAT('%', :term, '%')
              )
            """)
    List<Patient> searchActivePatients(String term);
}