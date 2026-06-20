package pe.edu.upeu.medical_appointment.dtos;

public record DoctorDto() {
    public record DoctorRequest(
            String name,
            String lastName,
            String email,
            String phone,
            Long specialityId
    ) {
    }

    public record DoctorResponse(
            Long id,
            String name,
            String lastName,
            String fullName,
            String email,
            String phone,
            Long specialityId,
            String specialityName
    ) {
    }

    public record DoctorComboResponse(
            Long id,
            String fullName
    ) {
    }
}
