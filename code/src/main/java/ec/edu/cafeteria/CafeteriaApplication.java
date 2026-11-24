package ec.edu.cafeteria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CafeteriaApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> System.setProperty(
                entry.getKey(),
                entry.getValue()));

        System.out.println("DB_URL: " + System.getProperty("DB_URL"));
        System.out.println("DB_USER: " + System.getProperty("DB_USER"));
        System.out.println("DB_PASSWORD: " +
                (System.getProperty("DB_PASSWORD") != null
                        ? "CARGADA"
                        : "NO ENCONTRADA"));

        SpringApplication.run(CafeteriaApplication.class, args);
    }
}
