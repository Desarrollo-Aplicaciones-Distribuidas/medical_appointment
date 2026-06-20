package pe.edu.upeu.medical_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.medical_appointment.entity.DayOfWeek;
import pe.edu.upeu.medical_appointment.entity.DoctorSchedule;

import java.time.LocalTime;
import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    List<DoctorSchedule> findByDoctorId(Long doctorId);

    List<DoctorSchedule> findByDoctorIdAndDayOfWeek(
            Long doctorId,
            DayOfWeek dayOfWeek
    );

    List<DoctorSchedule> findByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime endTime,
            LocalTime startTime
    );
}