package pe.edu.upeu.medical_appointment.services;

import pe.edu.upeu.medical_appointment.entity.Appointment;
import pe.edu.upeu.medical_appointment.entity.Status;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    List<Appointment> getAll();

    Appointment getById(Long id);

    List<Appointment> getByDoctorId(Long doctorId);

    List<Appointment> getByPatientId(Long patientId);

    List<Appointment> getByDate(LocalDate date);

    List<Appointment> getByDoctorIdAndDate(
            Long doctorId,
            LocalDate date
    );

    List<Appointment> getByPatientIdAndDate(
            Long patientId,
            LocalDate date
    );

    List<Appointment> getByStatus(Status status);

    List<Appointment> getByDoctorIdAndStatus(
            Long doctorId,
            Status status
    );

    List<Appointment> getByPatientIdAndStatus(
            Long patientId,
            Status status
    );

    Appointment create(Appointment appointment);

    Appointment update(Long id, Appointment appointment);

    void delete(Long id);
}