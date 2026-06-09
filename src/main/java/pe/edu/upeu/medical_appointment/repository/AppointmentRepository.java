package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.medical_appointment.entity.Appointment;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.entity.Patient;
import pe.edu.upeu.medical_appointment.entity.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findByDoctorIdAndAppointmentDate(
            Long doctorId,
            LocalDate appointmentDate
    );

    List<Appointment> findByPatientIdAndAppointmentDate(
            Long patientId,
            LocalDate appointmentDate
    );

    List<Appointment> findByStatus(Status status);

    /**
     * Obtiene todas las citas de un doctor para una fecha.
     */
    List<Appointment> findByDoctorIdAndAppointmentDateOrderByStartTime(
            Long doctorId,
            LocalDate appointmentDate
    );

    /**
     * Validación de cruces de horarios.
     */
    List<Appointment> findByDoctorIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime endTime,
            LocalTime startTime
    );
}