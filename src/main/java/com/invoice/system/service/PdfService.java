package com.invoice.system.service;

import java.io.ByteArrayInputStream;

public interface PdfService {
  ByteArrayInputStream generatePdf(String id);

  String getDocumentNumber(String id);
}
