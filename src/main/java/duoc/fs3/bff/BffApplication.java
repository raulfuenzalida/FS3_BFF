package duoc.fs3.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Clase principal de la aplicación BFF (Backend For Frontend).
 * 
 * Esta clase contiene el método main que inicia el microservicio BFF
 * utilizando Spring Boot. Actúa como gateway/proxy para enrutar peticiones
 * desde los frontends hacia los microservicios backend.
 * 
 * @author Duoc UC Fullstack III
 * @version 1.0
 * @since 2026
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class BffApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     * 
     * @param args Argumentos de línea de comandos pasados a la aplicación
     */
    public static void main(String[] args) {
        SpringApplication.run(BffApplication.class, args);
    }

}
