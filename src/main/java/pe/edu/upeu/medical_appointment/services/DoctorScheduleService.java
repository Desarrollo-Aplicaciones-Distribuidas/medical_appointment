package pe.edu.upeu.medical_appointment.services;

import pe.edu.upeu.medical_appointment.entity.DayOfWeek;
import pe.edu.upeu.medical_appointment.entity.DoctorSchedule;


import java.util.List;

public interface DoctorScheduleService {

    List<DoctorSchedule> getAll();

    DoctorSchedule getById(Long id);

    List<DoctorSchedule> getByDoctorId(Long doctorId);

    List<DoctorSchedule> getByDoctorIdAndDayOfWeek(
            Long doctorId,
            DayOfWeek dayOfWeek
    );

    DoctorSchedule create(DoctorSchedule doctorSchedule);

    DoctorSchedule update(Long id, DoctorSchedule doctorSchedule);

    void delete(Long id);
}