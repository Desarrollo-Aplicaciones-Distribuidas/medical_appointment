package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.medical_appointment.entity.Appointment;
import pe.edu.upeu.medical_appointment.entity.DayOfWeek;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.entity.DoctorSchedule;
import pe.edu.upeu.medical_appointment.entity.Patient;
import pe.edu.upeu.medical_appointment.entity.Status;
import pe.edu.upeu.medical_appointment.exceptions.ResourceNotFoundException;
import pe.edu.upeu.medical_appointment.repository.AppointmentRepository;
import pe.edu.upeu.medical_appointment.repository.DoctorRepository;
import pe.edu.upeu.medical_appointment.repository.DoctorScheduleRepository;
import pe.edu.upeu.medical_appointment.repository.PatientRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private static final List<Status> BLOCKING_STATUSES = List.of(
            Status.SCHEDULED,
            Status.CONFIRMED
    );

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorScheduleRepository doctorScheduleRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    @Override
    public List<Appointment> getAll() {
        return appointmentRepository.findAllByOrderByAppointmentDateAscStartTimeAsc();
    }

    @Override
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with id: " + id
                        )
                );
    }

    @Override
    public List<Appointment> getByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateAscStartTimeAsc(doctorId);
    }

    @Override
    public List<Appointment> getByPatientId(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateAscStartTimeAsc(patientId);
    }

    @Override
    public List<Appointment> getByPatientDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient dni is required");
        }

        Patient patient = patientRepository.findByDniAndDeletedFalse(dni.trim())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with dni: " + dni
                        )
                );

        return appointmentRepository.findByPatientIdOrderByAppointmentDateAscStartTimeAsc(
                patient.getId()
        );
    }

    @Override
    public List<Appointment> getByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDateOrderByStartTimeAsc(date);
    }

    @Override
    public List<Appointment> getByDoctorIdAndDate(
            Long doctorId,
            LocalDate date
    ) {
        return appointmentRepository.findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(
                doctorId,
                date
        );
    }

    @Override
    public List<Appointment> getByPatientIdAndDate(
            Long patientId,
            LocalDate date
    ) {
        return appointmentRepository.findByPatientIdAndAppointmentDateOrderByStartTimeAsc(
                patientId,
                date
        );
    }

    @Override
    public List<Appointment> getByStatus(Status status) {
        return appointmentRepository.findByStatusOrderByAppointmentDateAscStartTimeAsc(status);
    }

    @Override
    public List<Appointment> getByDoctorIdAndStatus(
            Long doctorId,
            Status status
    ) {
        return appointmentRepository.findByDoctorIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
                doctorId,
                status
        );
    }

    @Override
    public List<Appointment> getByPatientIdAndStatus(
            Long patientId,
            Status status
    ) {
        return appointmentRepository.findByPatientIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
                patientId,
                status
        );
    }

    @Override
    public Appointment create(Appointment appointment) {
        validateAppointment(appointment);

        Doctor doctor = findDoctorById(appointment.getDoctor().getId());
        Patient patient = findPatientById(appointment.getPatient().getId());

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(Status.SCHEDULED);

        validateAppointmentFitsDoctorSchedule(appointment);
        validateNoDoctorOverlap(null, appointment);
        validateNoPatientOverlap(null, appointment);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment update(Long id, Appointment appointment) {
        Appointment existing = findById(id);

        if (existing.getStatus() == Status.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede editar una cita cancelada"
            );
        }

        if (existing.getStatus() == Status.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede editar una cita completada"
            );
        }

        validateAppointment(appointment);

        Doctor doctor = findDoctorById(appointment.getDoctor().getId());
        Patient patient = findPatientById(appointment.getPatient().getId());

        existing.setReason(appointment.getReason());
        existing.setAppointmentDate(appointment.getAppointmentDate());
        existing.setStartTime(appointment.getStartTime());
        existing.setEndTime(appointment.getEndTime());
        existing.setDoctor(doctor);
        existing.setPatient(patient);

        validateAppointmentFitsDoctorSchedule(existing);
        validateNoDoctorOverlap(id, existing);
        validateNoPatientOverlap(id, existing);

        return appointmentRepository.save(existing);
    }

    @Override
    public Appointment cancel(Long id) {
        Appointment appointment = findById(id);

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
        Appointment appointment = findById(id);

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
        Appointment appointment = findById(id);

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
            throw new IllegalArgumentException("Appointment is required");
        }

        if (appointment.getReason() == null || appointment.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required");
        }

        if (appointment.getReason().trim().length() > 500) {
            throw new IllegalArgumentException("Reason must have at most 500 characters");
        }

        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException("Appointment date is required");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }

        if (appointment.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is required");
        }

        if (appointment.getEndTime() == null) {
            throw new IllegalArgumentException("End time is required");
        }

        if (!appointment.getStartTime().isBefore(appointment.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (appointment.getAppointmentDate().isEqual(LocalDate.now())
                && appointment.getStartTime().isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Appointment start time cannot be in the past");
        }

        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            throw new IllegalArgumentException("Doctor id is required");
        }

        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            throw new IllegalArgumentException("Patient id is required");
        }

        appointment.setReason(appointment.getReason().trim());
    }

    private void validateAppointmentFitsDoctorSchedule(Appointment appointment) {
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(
                appointment.getAppointmentDate().getDayOfWeek().name()
        );

        List<DoctorSchedule> schedules =
                doctorScheduleRepository.findByDoctorIdAndDayOfWeekAndDeletedFalseAndDoctorDeletedFalse(
                        appointment.getDoctor().getId(),
                        dayOfWeek
                );

        DoctorSchedule matchingSchedule = schedules.stream()
                .filter(schedule ->
                        !appointment.getStartTime().isBefore(schedule.getStartTime())
                                && !appointment.getEndTime().isAfter(schedule.getEndTime())
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "The doctor does not have availability for the selected date and time"
                        )
                );

        long appointmentMinutes = Duration.between(
                appointment.getStartTime(),
                appointment.getEndTime()
        ).toMinutes();

        if (appointmentMinutes != matchingSchedule.getAppointmentDuration()) {
            throw new IllegalArgumentException(
                    "Appointment duration must match the doctor's schedule duration"
            );
        }

        long minutesFromScheduleStart = Duration.between(
                matchingSchedule.getStartTime(),
                appointment.getStartTime()
        ).toMinutes();

        if (minutesFromScheduleStart % matchingSchedule.getAppointmentDuration() != 0) {
            throw new IllegalArgumentException(
                    "Appointment start time does not match an available schedule slot"
            );
        }
    }

    private void validateNoDoctorOverlap(
            Long appointmentId,
            Appointment appointment
    ) {
        boolean hasOverlap = appointmentId == null
                ? appointmentRepository.existsByDoctorIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                appointment.getEndTime(),
                appointment.getStartTime(),
                BLOCKING_STATUSES
        )
                : appointmentRepository.existsByDoctorIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThanAndStatusInAndIdNot(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                appointment.getEndTime(),
                appointment.getStartTime(),
                BLOCKING_STATUSES,
                appointmentId
        );

        if (hasOverlap) {
            throw new IllegalArgumentException(
                    "The doctor already has an active appointment in that time range"
            );
        }
    }

    private void validateNoPatientOverlap(
            Long appointmentId,
            Appointment appointment
    ) {
        boolean hasOverlap = appointmentId == null
                ? appointmentRepository.existsByPatientIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                appointment.getPatient().getId(),
                appointment.getAppointmentDate(),
                appointment.getEndTime(),
                appointment.getStartTime(),
                BLOCKING_STATUSES
        )
                : appointmentRepository.existsByPatientIdAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThanAndStatusInAndIdNot(
                appointment.getPatient().getId(),
                appointment.getAppointmentDate(),
                appointment.getEndTime(),
                appointment.getStartTime(),
                BLOCKING_STATUSES,
                appointmentId
        );

        if (hasOverlap) {
            throw new IllegalArgumentException(
                    "The patient already has an active appointment in that time range"
            );
        }
    }

    private Doctor findDoctorById(Long doctorId) {
        return doctorRepository.findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with id: " + doctorId
                        )
                );
    }

    private Patient findPatientById(Long patientId) {
        return patientRepository.findByIdAndDeletedFalse(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: " + patientId
                        )
                );
    }
}