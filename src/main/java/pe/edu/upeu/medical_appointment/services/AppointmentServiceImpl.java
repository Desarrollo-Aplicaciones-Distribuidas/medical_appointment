package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.medical_appointment.entity.Appointment;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.entity.Patient;
import pe.edu.upeu.medical_appointment.entity.Status;
import pe.edu.upeu.medical_appointment.exceptions.ResourceNotFoundException;
import pe.edu.upeu.medical_appointment.repository.AppointmentRepository;
import pe.edu.upeu.medical_appointment.repository.DoctorRepository;
import pe.edu.upeu.medical_appointment.repository.PatientRepository;

import java.time.LocalDate;
import java.util.List;

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
                        new ResourceNotFoundException(
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
    public List<Appointment> getByPatientDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Patient dni is required"
            );
        }

        Patient patient = patientRepository.findByDniAndDeletedFalse(
                        dni.trim()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with dni: " + dni
                        )
                );

        return appointmentRepository.findByPatientId(
                patient.getId()
        );
    }

    @Override
    public List<Appointment> getByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDateOrderByStartTime(date);
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
    public List<Appointment> getByPatientIdAndDate(
            Long patientId,
            LocalDate date
    ) {
        return appointmentRepository
                .findByPatientIdAndAppointmentDateOrderByStartTime(
                        patientId,
                        date
                );
    }

    @Override
    public List<Appointment> getByStatus(Status status) {
        return appointmentRepository
                .findByStatusOrderByAppointmentDateAscStartTimeAsc(status);
    }

    @Override
    public List<Appointment> getByDoctorIdAndStatus(
            Long doctorId,
            Status status
    ) {
        return appointmentRepository
                .findByDoctorIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
                        doctorId,
                        status
                );
    }

    @Override
    public List<Appointment> getByPatientIdAndStatus(
            Long patientId,
            Status status
    ) {
        return appointmentRepository
                .findByPatientIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
                        patientId,
                        status
                );
    }

    @Override
    public Appointment create(Appointment appointment) {
        validateAppointment(appointment);

        Long doctorId = appointment.getDoctor().getId();
        Long patientId = appointment.getPatient().getId();

        Doctor doctor = findDoctorById(doctorId);
        Patient patient = findPatientById(patientId);

        validateDoctorAvailability(
                null,
                doctorId,
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment update(
            Long id,
            Appointment appointment
    ) {
        Appointment existing = getById(id);

        validateAppointment(appointment);

        Long doctorId = appointment.getDoctor().getId();
        Long patientId = appointment.getPatient().getId();

        Doctor doctor = findDoctorById(doctorId);
        Patient patient = findPatientById(patientId);

        validateDoctorAvailability(
                id,
                doctorId,
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );

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

        existing.setPatient(patient);
        existing.setDoctor(doctor);

        return appointmentRepository.save(existing);
    }

    @Override
    public Appointment cancel(Long id) {
        Appointment appointment = getById(id);

        if (appointment.getStatus() == Status.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cita ya se encuentra cancelada"
            );
        }

        if (appointment.getStatus() == Status.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede cancelar una cita que ya fue completada"
            );
        }

        appointment.setStatus(Status.CANCELLED);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment confirm(Long id) {
        Appointment appointment = getById(id);

        if (appointment.getStatus() == Status.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede confirmar una cita cancelada"
            );
        }

        if (appointment.getStatus() == Status.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede confirmar una cita que ya fue completada"
            );
        }

        if (appointment.getStatus() == Status.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cita ya se encuentra confirmada"
            );
        }

        appointment.setStatus(Status.CONFIRMED);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment complete(Long id) {
        Appointment appointment = getById(id);

        if (appointment.getStatus() == Status.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede completar una cita cancelada"
            );
        }

        if (appointment.getStatus() == Status.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cita ya se encuentra completada"
            );
        }

        appointment.setStatus(Status.COMPLETED);

        return appointmentRepository.save(appointment);
    }

    private void validateAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException(
                    "Appointment is required"
            );
        }

        if (appointment.getReason() == null ||
                appointment.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Reason is required"
            );
        }

        if (appointment.getReason().length() > 500) {
            throw new IllegalArgumentException(
                    "Reason must have at most 500 characters"
            );
        }

        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException(
                    "Appointment date is required"
            );
        }

        if (appointment.getStartTime() == null) {
            throw new IllegalArgumentException(
                    "Start time is required"
            );
        }

        if (appointment.getEndTime() == null) {
            throw new IllegalArgumentException(
                    "End time is required"
            );
        }

        if (!appointment.getStartTime().isBefore(appointment.getEndTime())) {
            throw new IllegalArgumentException(
                    "Start time must be before end time"
            );
        }

        if (appointment.getStatus() == null) {
            throw new IllegalArgumentException(
                    "Status is required"
            );
        }

        if (appointment.getDoctor() == null ||
                appointment.getDoctor().getId() == null) {
            throw new IllegalArgumentException(
                    "Doctor id is required"
            );
        }

        if (appointment.getPatient() == null ||
                appointment.getPatient().getId() == null) {
            throw new IllegalArgumentException(
                    "Patient id is required"
            );
        }

        appointment.setReason(
                appointment.getReason().trim()
        );
    }

    private void validateDoctorAvailability(
            Long appointmentId,
            Long doctorId,
            LocalDate appointmentDate,
            java.time.LocalTime startTime,
            java.time.LocalTime endTime
    ) {
        List<Appointment> overlaps =
                appointmentRepository
                        .findByDoctorIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThan(
                                doctorId,
                                appointmentDate,
                                endTime,
                                startTime
                        );

        boolean hasOverlap =
                overlaps.stream()
                        .filter(existingAppointment ->
                                existingAppointment.getStatus() != Status.CANCELLED
                        )
                        .anyMatch(existingAppointment ->
                                appointmentId == null ||
                                        !existingAppointment.getId().equals(appointmentId)
                        );

        if (hasOverlap) {
            throw new IllegalArgumentException(
                    "The doctor already has an appointment scheduled in that time range"
            );
        }
    }

    private Doctor findDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with id: " + doctorId
                        )
                );
    }

    private Patient findPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: " + patientId
                        )
                );
    }
}