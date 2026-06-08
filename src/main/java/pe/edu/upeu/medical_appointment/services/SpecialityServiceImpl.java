package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.entity.Speciality;
import pe.edu.upeu.medical_appointment.repository.SpecialityRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class SpecialityServiceImpl implements SpecialityService {

    private final SpecialityRepository specialityRepository;

    public SpecialityServiceImpl(SpecialityRepository specialityRepository) {
        this.specialityRepository = specialityRepository;
    }

    @Override
    public List<Speciality> getAll() {
        return this.specialityRepository.findByDeletedFalse();
    }

    @Override
    public Speciality findById(Long id) {
        return this.specialityRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NoSuchElementException("Speciality with the ID " + id + " doesn't exists"));
    }

    @Override
    public Speciality create(Speciality speciality) {
        speciality.setDeleted(Boolean.FALSE);
        return this.specialityRepository.save(speciality);
    }

    @Override
    public Speciality update(Long id, Speciality speciality) {
        var specialityToUpdate = this.findById(speciality.getId());
        specialityToUpdate.setName(speciality.getName());
        specialityToUpdate.setDescription(speciality.getDescription());

        return this.specialityRepository.save(specialityToUpdate);
    }

    @Override
    public Speciality delete(Long id) {
        var specialityToDelete = this.findById(id);
        specialityToDelete.setDeleted(true);
        return this.specialityRepository.save(specialityToDelete);
    }
}
