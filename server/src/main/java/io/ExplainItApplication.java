package io;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"io.explainit"})
public class ExplainItApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExplainItApplication.class, args);
	}

}
