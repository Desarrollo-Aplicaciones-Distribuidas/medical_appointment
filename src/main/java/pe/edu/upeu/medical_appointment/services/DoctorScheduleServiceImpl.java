package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.entity.DayOfWeek;
import pe.edu.upeu.medical_appointment.entity.DoctorSchedule;
import pe.edu.upeu.medical_appointment.exceptions.ResourceNotFoundException;
import pe.edu.upeu.medical_appointment.repository.DoctorScheduleRepository;

import java.util.List;

@Service
@Transactional
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;

    public DoctorScheduleServiceImpl(
            DoctorScheduleRepository doctorScheduleRepository
    ) {
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    @Override
    public List<DoctorSchedule> getAll() {
        return doctorScheduleRepository.findAll();
    }

    @Override
    public DoctorSchedule getById(Long id) {
        return doctorScheduleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor schedule not found with id: " + id
                        )
                );
    }

    @Override
    public List<DoctorSchedule> getByDoctorId(Long doctorId) {
        return doctorScheduleRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<DoctorSchedule> getByDoctorIdAndDayOfWeek(
            Long doctorId,
            DayOfWeek dayOfWeek
    ) {
        return doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(
                        doctorId,
                        dayOfWeek
                );
    }

    @Override
    public DoctorSchedule create(
            DoctorSchedule doctorSchedule
    ) {
        Long doctorId = doctorSchedule.getDoctor().getId();

        List<DoctorSchedule> overlaps =
                doctorScheduleRepository
                        .findByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                                doctorId,
                                doctorSchedule.getDayOfWeek(),
                                doctorSchedule.getEndTime(),
                                doctorSchedule.getStartTime()
                        );

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException(
                    "The doctor already has a schedule in that time range"
            );
        }

        return doctorScheduleRepository.save(doctorSchedule);
    }

    @Override
    public DoctorSchedule update(
            Long id,
            DoctorSchedule doctorSchedule
    ) {
        DoctorSchedule existing = getById(id);

        Long doctorId = doctorSchedule.getDoctor().getId();

        List<DoctorSchedule> overlaps =
                doctorScheduleRepository
                        .findByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                                doctorId,
                                doctorSchedule.getDayOfWeek(),
                                doctorSchedule.getEndTime(),
                                doctorSchedule.getStartTime()
                        );

        boolean hasOverlapWithAnotherSchedule =
                overlaps.stream()
                        .anyMatch(schedule ->
                                !schedule.getId().equals(id)
                        );

        if (hasOverlapWithAnotherSchedule) {
            throw new IllegalArgumentException(
                    "The doctor already has a schedule in that time range"
            );
        }

        existing.setDayOfWeek(
                doctorSchedule.getDayOfWeek()
        );

        existing.setStartTime(
                doctorSchedule.getStartTime()
        );

        existing.setEndTime(
                doctorSchedule.getEndTime()
        );

        existing.setAppointmentDuration(
                doctorSchedule.getAppointmentDuration()
        );

        existing.setDoctor(
                doctorSchedule.getDoctor()
        );

        return doctorScheduleRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        DoctorSchedule existing = getById(id);
        doctorScheduleRepository.delete(existing);
    }
}