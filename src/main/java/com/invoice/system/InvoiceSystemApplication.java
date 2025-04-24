package com.invoice.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InvoiceSystemApplication {

  public static void main(String[] args) {
    SpringApplication.run(InvoiceSystemApplication.class, args);
  }
}
