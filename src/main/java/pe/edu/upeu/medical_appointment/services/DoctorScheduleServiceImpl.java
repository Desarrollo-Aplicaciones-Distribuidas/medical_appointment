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
                .findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);
    }

    @Override
    public DoctorSchedule create(DoctorSchedule doctorSchedule) {
        return doctorScheduleRepository.save(doctorSchedule);
    }

    @Override
    public DoctorSchedule update(
            Long id,
            DoctorSchedule doctorSchedule
    ) {

        DoctorSchedule existing = getById(id);

        existing.setDayOfWeek(
                doctorSchedule.getDayOfWeek()
        );

        existing.setStartTime(
                doctorSchedule.getStartTime()
        );

        existing.setEndTime(
                doctorSchedule.getEndTime()
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