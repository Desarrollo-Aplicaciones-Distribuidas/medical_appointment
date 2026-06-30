package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.medical_appointment.entity.Appointment;
import pe.edu.upeu.medical_appointment.entity.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByOrderByAppointmentDateAscStartTimeAsc();

    List<Appointment> findByDoctorIdOrderByAppointmentDateAscStartTimeAsc(
            Long doctorId
    );

    List<Appointment> findByPatientIdOrderByAppointmentDateAscStartTimeAsc(
            Long patientId
    );

    List<Appointment> findByAppointmentDateOrderByStartTimeAsc(
            LocalDate appointmentDate
    );

    List<Appointment> findByStatusOrderByAppointmentDateAscStartTimeAsc(
            Status status
    );

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(
            Long doctorId,
            LocalDate appointmentDate
    );

    List<Appointment> findByPatientIdAndAppointmentDateOrderByStartTimeAsc(
            Long patientId,
            LocalDate appointmentDate
    );

    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
            Long doctorId,
            Status status
    );

    List<Appointment> findByPatientIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
            Long patientId,
            Status status
    );

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusInOrderByStartTimeAsc(
            Long doctorId,
            LocalDate appointmentDate,
            Collection<Status> statuses
    );

    boolean existsByDoctorIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime endTime,
            LocalTime startTime,
            Collection<Status> statuses
    );

    boolean existsByPatientIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
            Long patientId,
            LocalDate appointmentDate,
            LocalTime endTime,
            LocalTime startTime,
            Collection<Status> statuses
    );

    boolean existsByDoctorIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThanAndStatusInAndIdNot(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime endTime,
            LocalTime startTime,
            Collection<Status> statuses,
            Long id
    );

    boolean existsByPatientIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThanAndStatusInAndIdNot(
            Long patientId,
            LocalDate appointmentDate,
            LocalTime endTime,
            LocalTime startTime,
            Collection<Status> statuses,
            Long id
    );
}