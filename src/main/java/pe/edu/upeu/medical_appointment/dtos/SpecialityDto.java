package pe.edu.upeu.medical_appointment.dtos;

public record SpecialityDto() {
    public record SpecialityRequest(
            String name,
            String description
    ) {
    }

    public record SpecialityResponse(
            Long id,
            String name,
            String description
    ) {
    }

    public record SpecialityComboResponse(
            Long id,
            String name
    ) {
    }
}
