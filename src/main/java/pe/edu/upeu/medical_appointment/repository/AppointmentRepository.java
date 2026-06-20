package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.medical_appointment.entity.Appointment;
import pe.edu.upeu.medical_appointment.entity.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(
            Long doctorId
    );

    List<Appointment> findByPatientId(
            Long patientId
    );

    List<Appointment> findByAppointmentDate(
            LocalDate appointmentDate
    );

    List<Appointment> findByStatus(
            Status status
    );

    List<Appointment> findByDoctorIdAndAppointmentDate(
            Long doctorId,
            LocalDate appointmentDate
    );

    List<Appointment> findByPatientIdAndAppointmentDate(
            Long patientId,
            LocalDate appointmentDate
    );

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByStartTime(
            Long doctorId,
            LocalDate appointmentDate
    );

    List<Appointment> findByPatientIdAndAppointmentDateOrderByStartTime(
            Long patientId,
            LocalDate appointmentDate
    );

    List<Appointment> findByAppointmentDateOrderByStartTime(
            LocalDate appointmentDate
    );

    List<Appointment> findByStatusOrderByAppointmentDateAscStartTimeAsc(
            Status status
    );

    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
            Long doctorId,
            Status status
    );

    List<Appointment> findByPatientIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
            Long patientId,
            Status status
    );

    List<Appointment> findByDoctorIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime endTime,
            LocalTime startTime
    );
}