package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.exceptions.ResourceNotFoundException;
import pe.edu.upeu.medical_appointment.repository.DoctorRepository;

import java.util.List;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<Doctor> getAll() {
        return this.doctorRepository.findByDeletedFalse();
    }

    @Override
    public Doctor create(Doctor doctor) {
        doctor.setDeleted(Boolean.FALSE);
        return this.doctorRepository.save(doctor);
    }

    @Override
    public Doctor findByName(String name) {
        return this.doctorRepository.findByNameAndDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    @Override
    public Doctor update(String name, Doctor doctor) {
        var doctorToUpdate = this.findByName(name);

        doctorToUpdate.setName(doctor.getName());
        doctorToUpdate.setLastName(doctor.getLastName());
        doctorToUpdate.setEmail(doctor.getEmail());
        doctorToUpdate.setPhone(doctor.getPhone());

        return this.doctorRepository.save(doctorToUpdate);
    }

    @Override
    public Doctor delete(String name) {
        var doctorToDelete = this.findByName(name);
        doctorToDelete.setDeleted(Boolean.TRUE);
        return this.doctorRepository.save(doctorToDelete);
    }
}
