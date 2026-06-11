package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.entity.Appointment;
import pe.edu.upeu.medical_appointment.entity.Status;
import pe.edu.upeu.medical_appointment.repository.AppointmentRepository;
import pe.edu.upeu.medical_appointment.repository.DoctorRepository;
import pe.edu.upeu.medical_appointment.repository.PatientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Appointment not found with id: " + id
                        )
                );
    }

    @Override
    public List<Appointment> getByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> getByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }

    @Override
    public List<Appointment> getByDoctorIdAndDate(
            Long doctorId,
            LocalDate date
    ) {
        return appointmentRepository
                .findByDoctorIdAndAppointmentDateOrderByStartTime(
                        doctorId,
                        date
                );
    }

    @Override
    public List<Appointment> getByStatus(Status status) {
        return appointmentRepository.findByStatus(status);
    }

    @Override
    public Appointment create(Appointment appointment) {

        validateAppointment(appointment);

        Long doctorId = appointment.getDoctor().getId();

        List<Appointment> overlaps =
                appointmentRepository
                        .findByDoctorIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThan(
                                doctorId,
                                appointment.getAppointmentDate(),
                                appointment.getEndTime(),
                                appointment.getStartTime()
                        );

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException(
                    "The doctor already has an appointment scheduled in that time range"
            );
        }

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment update(
            Long id,
            Appointment appointment
    ) {

        Appointment existing = getById(id);

        validateAppointment(appointment);

        existing.setReason(
                appointment.getReason()
        );

        existing.setAppointmentDate(
                appointment.getAppointmentDate()
        );

        existing.setStartTime(
                appointment.getStartTime()
        );

        existing.setEndTime(
                appointment.getEndTime()
        );

        existing.setStatus(
                appointment.getStatus()
        );

        existing.setPatient(
                appointment.getPatient()
        );

        existing.setDoctor(
                appointment.getDoctor()
        );

        return appointmentRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Appointment existing = getById(id);
        appointmentRepository.delete(existing);
    }

    private void validateAppointment(
            Appointment appointment
    ) {

        if (appointment.getStartTime()
                .isAfter(appointment.getEndTime())) {

            throw new IllegalArgumentException(
                    "Start time cannot be after end time"
            );
        }

        if (appointment.getStartTime()
                .equals(appointment.getEndTime())) {

            throw new IllegalArgumentException(
                    "Start time and end time cannot be equal"
            );
        }

        Long doctorId = appointment.getDoctor().getId();

        doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Doctor not found with id: " + doctorId
                        )
                );

        Long patientId = appointment.getPatient().getId();

        patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Patient not found with id: " + patientId
                        )
                );
    }
}