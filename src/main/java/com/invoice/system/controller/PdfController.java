package com.invoice.system.controller;

import com.invoice.system.service.PdfService;
import java.io.ByteArrayInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download-pdf/{id}")
@RequiredArgsConstructor
public class PdfController {

  private final PdfService pdfService;

  @GetMapping
  public ResponseEntity<InputStreamResource> downloadPdfById(@PathVariable String id)
      throws Exception {
    ByteArrayInputStream pdfBytes = pdfService.generatePdf(id);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=" + pdfService.getDocumentNumber(id) + ".pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(new InputStreamResource(pdfBytes));
  }
}
