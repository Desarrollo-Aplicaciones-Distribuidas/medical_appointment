package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.medical_appointment.entity.DayOfWeek;
import pe.edu.upeu.medical_appointment.entity.DoctorSchedule;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    List<DoctorSchedule> findByDeletedFalseAndDoctorDeletedFalse();

    Optional<DoctorSchedule> findByIdAndDeletedFalseAndDoctorDeletedFalse(Long id);

    List<DoctorSchedule> findByDoctorIdAndDeletedFalseAndDoctorDeletedFalse(Long doctorId);

    List<DoctorSchedule> findByDoctorIdAndDeletedFalse(Long doctorId);

    List<DoctorSchedule> findByDoctorIdAndDayOfWeekAndDeletedFalseAndDoctorDeletedFalse(
            Long doctorId,
            DayOfWeek dayOfWeek
    );

    List<DoctorSchedule> findByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndDeletedFalse(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime endTime,
            LocalTime startTime
    );

    List<DoctorSchedule> findByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndIdNotAndDeletedFalse(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime endTime,
            LocalTime startTime,
            Long id
    );
}