package pe.edu.upeu.medical_appointment.dtos;

public record PatientDto() {

    public record PatientRequest(
            String name,
            String lastName,
            String dni,
            String email,
            String phone
    ) {
    }

    public record PatientResponse(
            Long id,
            String name,
            String lastName,
            String fullName,
            String dni,
            String email,
            String phone
    ) {
    }

    public record PatientComboResponse(
            Long id,
            String fullName,
            String dni
    ) {
    }
}