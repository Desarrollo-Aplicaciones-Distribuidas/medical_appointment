package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.entity.Doctor;
import pe.edu.upeu.medical_appointment.entity.Speciality;
import pe.edu.upeu.medical_appointment.exceptions.ResourceNotFoundException;
import pe.edu.upeu.medical_appointment.repository.DoctorRepository;
import pe.edu.upeu.medical_appointment.repository.DoctorScheduleRepository;
import pe.edu.upeu.medical_appointment.repository.SpecialityRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialityRepository specialityRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    public DoctorServiceImpl(
            DoctorRepository doctorRepository,
            SpecialityRepository specialityRepository,
            DoctorScheduleRepository doctorScheduleRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.specialityRepository = specialityRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    @Override
    public List<Doctor> getAll() {
        return doctorRepository.findByDeletedFalse();
    }

    @Override
    public List<Doctor> getBySpecialityId(Long specialityId) {
        return doctorRepository.findBySpecialityIdAndDeletedFalse(specialityId);
    }

    @Override
    public Doctor findById(Long id) {
        return doctorRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with id: " + id
                        )
                );
    }

    @Override
    public Doctor findByEmail(String email) {
        return doctorRepository.findByEmailIgnoreCaseAndDeletedFalse(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with email: " + email
                        )
                );
    }

    @Override
    public Doctor create(Doctor doctor) {
        validateDoctor(doctor);

        if (hasEmail(doctor)) {
            Optional<Doctor> deletedDoctor =
                    doctorRepository.findByEmailIgnoreCaseAndDeletedTrue(doctor.getEmail());

            if (deletedDoctor.isPresent()) {
                Doctor doctorToRestore = deletedDoctor.get();

                doctorToRestore.setName(doctor.getName());
                doctorToRestore.setLastName(doctor.getLastName());
                doctorToRestore.setEmail(doctor.getEmail());
                doctorToRestore.setPhone(doctor.getPhone());
                doctorToRestore.setSpeciality(doctor.getSpeciality());
                doctorToRestore.setDeleted(Boolean.FALSE);

                return doctorRepository.save(doctorToRestore);
            }

            if (doctorRepository.existsByEmailIgnoreCaseAndDeletedFalse(doctor.getEmail())) {
                throw new IllegalArgumentException(
                        "A doctor with email " + doctor.getEmail() + " already exists"
                );
            }
        }

        doctor.setDeleted(Boolean.FALSE);

        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor update(Long id, Doctor doctor) {
        Doctor doctorToUpdate = findById(id);

        validateDoctor(doctor);

        boolean emailChanged = hasEmail(doctor)
                && (
                doctorToUpdate.getEmail() == null
                        || !doctorToUpdate.getEmail().equalsIgnoreCase(doctor.getEmail())
        );

        if (emailChanged && doctorRepository.existsByEmailIgnoreCaseAndDeletedFalse(doctor.getEmail())) {
            throw new IllegalArgumentException(
                    "A doctor with email " + doctor.getEmail() + " already exists"
            );
        }

        doctorToUpdate.setName(doctor.getName());
        doctorToUpdate.setLastName(doctor.getLastName());
        doctorToUpdate.setEmail(doctor.getEmail());
        doctorToUpdate.setPhone(doctor.getPhone());
        doctorToUpdate.setSpeciality(doctor.getSpeciality());

        return doctorRepository.save(doctorToUpdate);
    }

    @Override
    public Doctor delete(Long id) {
        Doctor doctorToDelete = findById(id);

        doctorScheduleRepository.findByDoctorIdAndDeletedFalse(id)
                .forEach(schedule -> schedule.setDeleted(Boolean.TRUE));

        doctorToDelete.setDeleted(Boolean.TRUE);

        return doctorRepository.save(doctorToDelete);
    }

    private void validateDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor is required");
        }

        if (doctor.getName() == null || doctor.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name is required");
        }

        if (doctor.getLastName() == null || doctor.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor last name is required");
        }

        if (doctor.getSpeciality() == null || doctor.getSpeciality().getId() == null) {
            throw new IllegalArgumentException("Doctor speciality is required");
        }

        Speciality speciality = specialityRepository.findByIdAndDeletedFalse(
                        doctor.getSpeciality().getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Speciality not found with id: " + doctor.getSpeciality().getId()
                        )
                );

        doctor.setName(doctor.getName().trim());
        doctor.setLastName(doctor.getLastName().trim());
        doctor.setSpeciality(speciality);

        if (doctor.getEmail() != null) {
            String email = doctor.getEmail().trim();
            doctor.setEmail(email.isEmpty() ? null : email);
        }

        if (doctor.getPhone() != null) {
            String phone = doctor.getPhone().trim();
            doctor.setPhone(phone.isEmpty() ? null : phone);
        }
    }

    private boolean hasEmail(Doctor doctor) {
        return doctor.getEmail() != null && !doctor.getEmail().trim().isEmpty();
    }
}