package pe.edu.upeu.medical_appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MedicalAppointmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalAppointmentApplication.class, args);
	}

}
