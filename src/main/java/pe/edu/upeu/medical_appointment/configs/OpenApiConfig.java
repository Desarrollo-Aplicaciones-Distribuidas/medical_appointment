package pe.edu.upeu.medical_appointment.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Medical Appointment CRUD",
                version = "1.0.0",
                description = "This is a CRUD for Management Medical Appointment"
        )
)
public class OpenApiConfig {
}
