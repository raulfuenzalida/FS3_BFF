package duoc.fs3.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuración web para el BFF.
 * 
 * Esta clase configura CORS para permitir solicitudes desde los frontends
 * (FullStack_3_FrontEnd y PanelAdmin) hacia el BFF.
 * 
 * @author Duoc UC Fullstack III
 * @version 1.0
 * @since 2026
 */
@Configuration
public class WebConfig {

    /**
     * Configura el filtro CORS para permitir solicitudes desde los orígenes permitidos.
     * 
     * @return CorsFilter configurado
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Orígenes permitidos (frontends)
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:8084",      // PanelAdmin
            "http://localhost:19000",     // Expo (Android/iOS simulator)
            "http://localhost:19006",     // Expo Web
            "http://localhost:3000",      // Desarrollo web
            "http://localhost:4200"       // Angular (si aplica)
        ));
        
        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // Headers permitidos (todos)
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Permitir credentials (para JWT)
        config.setAllowCredentials(true);
        
        // Expose headers (si es necesario)
        config.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
