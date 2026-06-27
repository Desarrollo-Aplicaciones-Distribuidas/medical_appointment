package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.dtos.DoctorScheduleDto;
import pe.edu.upeu.medical_appointment.entity.DayOfWeek;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.entity.DoctorSchedule;
import pe.edu.upeu.medical_appointment.exceptions.ResourceNotFoundException;
import pe.edu.upeu.medical_appointment.repository.DoctorRepository;
import pe.edu.upeu.medical_appointment.repository.DoctorScheduleRepository;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;

    public DoctorScheduleServiceImpl(
            DoctorScheduleRepository doctorScheduleRepository,
            DoctorRepository doctorRepository
    ) {
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<DoctorSchedule> getAll() {
        return doctorScheduleRepository.findByDeletedFalseAndDoctorDeletedFalse();
    }

    @Override
    public DoctorSchedule findById(Long id) {
        return doctorScheduleRepository.findByIdAndDeletedFalseAndDoctorDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor schedule not found with id: " + id
                        )
                );
    }

    @Override
    public List<DoctorSchedule> getByDoctorId(Long doctorId) {
        return doctorScheduleRepository.findByDoctorIdAndDeletedFalseAndDoctorDeletedFalse(doctorId);
    }

    @Override
    public List<DoctorSchedule> getByDoctorIdAndDayOfWeek(
            Long doctorId,
            DayOfWeek dayOfWeek
    ) {
        return doctorScheduleRepository.findByDoctorIdAndDayOfWeekAndDeletedFalseAndDoctorDeletedFalse(
                doctorId,
                dayOfWeek
        );
    }

    @Override
    public DoctorSchedule create(DoctorSchedule doctorSchedule) {
        validateDoctorSchedule(doctorSchedule);
        validateNoOverlap(doctorSchedule);

        doctorSchedule.setDeleted(Boolean.FALSE);

        return doctorScheduleRepository.save(doctorSchedule);
    }

    @Override
    public List<DoctorSchedule> createBulk(
            DoctorScheduleDto.DoctorScheduleBulkRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Doctor schedule bulk request is required");
        }

        if (request.daysOfWeek() == null || request.daysOfWeek().isEmpty()) {
            throw new IllegalArgumentException("Select at least one day");
        }

        List<DoctorSchedule> schedules = request.daysOfWeek()
                .stream()
                .distinct()
                .map(dayOfWeek -> {
                    Doctor doctor = new Doctor();
                    doctor.setId(request.doctorId());

                    DoctorSchedule doctorSchedule = new DoctorSchedule();

                    doctorSchedule.setDoctor(doctor);
                    doctorSchedule.setDayOfWeek(dayOfWeek);
                    doctorSchedule.setStartTime(request.startTime());
                    doctorSchedule.setEndTime(request.endTime());
                    doctorSchedule.setAppointmentDuration(request.appointmentDuration());
                    doctorSchedule.setDeleted(Boolean.FALSE);

                    validateDoctorSchedule(doctorSchedule);
                    validateNoOverlap(doctorSchedule);

                    return doctorSchedule;
                })
                .toList();

        return doctorScheduleRepository.saveAll(schedules);
    }

    @Override
    public DoctorSchedule update(Long id, DoctorSchedule doctorSchedule) {
        DoctorSchedule existing = findById(id);

        validateDoctorSchedule(doctorSchedule);

        Long doctorId = doctorSchedule.getDoctor().getId();

        List<DoctorSchedule> overlaps =
                doctorScheduleRepository
                        .findByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndIdNotAndDeletedFalse(
                                doctorId,
                                doctorSchedule.getDayOfWeek(),
                                doctorSchedule.getEndTime(),
                                doctorSchedule.getStartTime(),
                                id
                        );

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException(
                    "The doctor already has a schedule in that time range"
            );
        }

        existing.setDayOfWeek(doctorSchedule.getDayOfWeek());
        existing.setStartTime(doctorSchedule.getStartTime());
        existing.setEndTime(doctorSchedule.getEndTime());
        existing.setAppointmentDuration(doctorSchedule.getAppointmentDuration());
        existing.setDoctor(doctorSchedule.getDoctor());

        return doctorScheduleRepository.save(existing);
    }

    @Override
    public DoctorSchedule delete(Long id) {
        DoctorSchedule existing = findById(id);

        existing.setDeleted(Boolean.TRUE);

        return doctorScheduleRepository.save(existing);
    }

    private void validateDoctorSchedule(DoctorSchedule doctorSchedule) {
        if (doctorSchedule == null) {
            throw new IllegalArgumentException("Doctor schedule is required");
        }

        if (doctorSchedule.getDoctor() == null || doctorSchedule.getDoctor().getId() == null) {
            throw new IllegalArgumentException("Doctor is required");
        }

        Doctor doctor = doctorRepository.findByIdAndDeletedFalse(
                        doctorSchedule.getDoctor().getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with id: " + doctorSchedule.getDoctor().getId()
                        )
                );

        if (doctorSchedule.getDayOfWeek() == null) {
            throw new IllegalArgumentException("Day of week is required");
        }

        if (doctorSchedule.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is required");
        }

        if (doctorSchedule.getEndTime() == null) {
            throw new IllegalArgumentException("End time is required");
        }

        if (!doctorSchedule.getStartTime().isBefore(doctorSchedule.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (doctorSchedule.getAppointmentDuration() == null) {
            throw new IllegalArgumentException("Appointment duration is required");
        }

        if (doctorSchedule.getAppointmentDuration() <= 0) {
            throw new IllegalArgumentException("Appointment duration must be greater than zero");
        }

        long totalMinutes = Duration.between(
                doctorSchedule.getStartTime(),
                doctorSchedule.getEndTime()
        ).toMinutes();

        if (doctorSchedule.getAppointmentDuration() > totalMinutes) {
            throw new IllegalArgumentException(
                    "Appointment duration cannot be greater than the schedule time range"
            );
        }

        if (totalMinutes % doctorSchedule.getAppointmentDuration() != 0) {
            throw new IllegalArgumentException(
                    "Appointment duration must divide the schedule time range exactly"
            );
        }

        doctorSchedule.setDoctor(doctor);
    }

    private void validateNoOverlap(DoctorSchedule doctorSchedule) {
        Long doctorId = doctorSchedule.getDoctor().getId();

        List<DoctorSchedule> overlaps =
                doctorScheduleRepository
                        .findByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndDeletedFalse(
                                doctorId,
                                doctorSchedule.getDayOfWeek(),
                                doctorSchedule.getEndTime(),
                                doctorSchedule.getStartTime()
                        );

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException(
                    "The doctor already has a schedule in that time range for "
                            + doctorSchedule.getDayOfWeek()
            );
        }
    }
}