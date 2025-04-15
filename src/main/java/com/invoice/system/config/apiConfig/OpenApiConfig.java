package com.invoice.system.config.apiConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
public class OpenApiConfig {

  @RestController
  @RequestMapping("/v1/doc/customer-vendor")
  public class CustomerVendorController {
    @GetMapping
    public String getOpenApiYaml() throws IOException {
      String data = "";
      ClassPathResource cpr = new ClassPathResource("customer_vendor.yaml");
      byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
      data = new String(bdata, StandardCharsets.UTF_8);
      return data;
    }
  }

  @RestController
  @RequestMapping("/v1/doc/quote")
  public class QuoteController {
    @GetMapping
    public String getOpenApiYaml() throws IOException {
      String data = "";
      ClassPathResource cpr = new ClassPathResource("quote.yaml");
      byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
      data = new String(bdata, StandardCharsets.UTF_8);
      return data;
    }
  }

  @RestController
  @RequestMapping("/v1/doc/po-invoice")
  public class POInvoiceController {
    @GetMapping
    public String getOpenApiYaml() throws IOException {
      String data = "";
      ClassPathResource cpr = new ClassPathResource("PO_Invoice.yaml");
      byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
      data = new String(bdata, StandardCharsets.UTF_8);
      return data;
    }
  }

  @RestController
  @RequestMapping("/v1/doc/pdf")
  public class PdfController {
    @GetMapping
    public String getOpenApiYaml() throws IOException {
      String data = "";
      ClassPathResource cpr = new ClassPathResource("pdf.yaml");
      byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
      data = new String(bdata, StandardCharsets.UTF_8);
      return data;
    }
  }
}
