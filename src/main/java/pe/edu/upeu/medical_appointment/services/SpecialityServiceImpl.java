package pe.edu.upeu.medical_appointment.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.medical_appointment.entity.Speciality;
import pe.edu.upeu.medical_appointment.exceptions.ResourceNotFoundException;
import pe.edu.upeu.medical_appointment.repository.SpecialityRepository;

import java.util.List;

@Service
@Transactional
public class SpecialityServiceImpl implements SpecialityService {

    private final SpecialityRepository specialityRepository;

    public SpecialityServiceImpl(
            SpecialityRepository specialityRepository
    ) {
        this.specialityRepository = specialityRepository;
    }

    @Override
    public List<Speciality> getAll() {
        return specialityRepository.findByDeletedFalse();
    }

    @Override
    public Speciality findById(
            Long id
    ) {
        return specialityRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Speciality not found with id: " + id
                        )
                );
    }

    @Override
    public Speciality findByName(
            String name
    ) {
        return specialityRepository.findByNameIgnoreCaseAndDeletedFalse(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Speciality not found with name: " + name
                        )
                );
    }

    @Override
    public Speciality create(
            Speciality speciality
    ) {
        validateSpeciality(speciality);

        if (specialityRepository.existsByNameIgnoreCaseAndDeletedFalse(
                speciality.getName()
        )) {
            throw new IllegalArgumentException(
                    "A speciality with name " + speciality.getName() + " already exists"
            );
        }

        speciality.setDeleted(Boolean.FALSE);

        return specialityRepository.save(speciality);
    }

    @Override
    public Speciality update(
            Long id,
            Speciality speciality
    ) {
        Speciality specialityToUpdate =
                findById(id);

        validateSpeciality(speciality);

        if (!specialityToUpdate.getName().equalsIgnoreCase(
                speciality.getName()
        ) && specialityRepository.existsByNameIgnoreCaseAndDeletedFalse(
                speciality.getName()
        )) {
            throw new IllegalArgumentException(
                    "A speciality with name " + speciality.getName() + " already exists"
            );
        }

        specialityToUpdate.setName(
                speciality.getName()
        );

        specialityToUpdate.setDescription(
                speciality.getDescription()
        );

        return specialityRepository.save(specialityToUpdate);
    }

    @Override
    public Speciality delete(
            Long id
    ) {
        Speciality specialityToDelete =
                findById(id);

        specialityToDelete.setDeleted(Boolean.TRUE);

        return specialityRepository.save(specialityToDelete);
    }

    private void validateSpeciality(
            Speciality speciality
    ) {
        if (speciality == null) {
            throw new IllegalArgumentException(
                    "Speciality is required"
            );
        }

        if (speciality.getName() == null ||
                speciality.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Speciality name is required"
            );
        }

        speciality.setName(
                speciality.getName().trim()
        );

        if (speciality.getDescription() != null) {
            speciality.setDescription(
                    speciality.getDescription().trim()
            );
        }
    }
}