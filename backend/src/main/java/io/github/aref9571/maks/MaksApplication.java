package io.github.aref9571.maks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MaksApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaksApplication.class, args);
	}

}
