package ro.mpp2024.start;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"ro.mpp2024"})

public class StartRestServices {
    public static void main(String[] args)
    {
        SpringApplication.run(StartRestServices.class, args);
    }
}
