package com.invoice.system.service.impl;

import com.invoice.system.model.InvoiceEntity;
import com.invoice.system.model.ItemEntity;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class InvoicePdfGeneration {
  private static final DeviceRgb DARK_BLUE = new DeviceRgb(7, 23, 123);
  private static final DeviceRgb LIGHT_BLUE = new DeviceRgb(173, 216, 230);
  private static final DeviceRgb BLACK = new DeviceRgb(0, 0, 0);

  @SneakyThrows
  public ByteArrayInputStream generateInvoicePdf(InvoiceEntity invoiceEntity) {
    PdfFont font = loadUnicodeFont();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(outputStream);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);
    Div mainContainer = new Div().setBorder(new SolidBorder(BLACK, 3));
    mainContainer.add(createHeader(invoiceEntity, font));

    mainContainer.add(createCompanyDetails(invoiceEntity, font).setMarginTop(0));

    mainContainer.add(createBillToSection(invoiceEntity, font).setMarginTop(0));

    mainContainer.add(createItemsTable(invoiceEntity, font).setMarginTop(0));

    mainContainer.add(createTotalAndTermsSection(invoiceEntity, font));

    mainContainer.add(createTotalAmountBox(invoiceEntity, font));

    mainContainer.add(createFooter(font));

    document.add(mainContainer);
    document.close();
    return new ByteArrayInputStream(outputStream.toByteArray());
  }

  public void savePdfToFile(byte[] pdfBytes, String filePath) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      fos.write(pdfBytes);
    }
  }

  private Table createHeader(InvoiceEntity invoiceEntity, PdfFont font) throws IOException {
    Table headerTable =
        new Table(2).setWidth(UnitValue.createPercentValue(100)).setBackgroundColor(DARK_BLUE);

    Cell leftCell =
        new Cell()
            .add(
                new Paragraph("TAX INVOICE")
                    .setFontColor(ColorConstants.WHITE)
                    .setFontSize(20)
                    .setPadding(5)
                    .setFont(font))
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT);

    Cell rightCell =
        new Cell()
            .add(
                new Paragraph(
                        "INVOICE NO:"
                            + invoiceEntity.getInvoiceNumber()
                            + "\nDATE: "
                            + formatDate(invoiceEntity.getInvoiceDate()))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(ColorConstants.WHITE))
            .setBorder(Border.NO_BORDER);

    headerTable.addCell(leftCell);
    headerTable.addCell(rightCell);
    return headerTable;
  }

  private Div createCompanyDetails(InvoiceEntity invoiceEntity, PdfFont font) {
    return new Div()
        .setBorder(new SolidBorder(1))
        .setPadding(1)
        .add(
            new Paragraph(
                "COMPANY NAME:"
                    + invoiceEntity.getPurchaseOrder().getVendor().getVendorName()
                    + "\nADDRESS:"
                    + invoiceEntity.getPurchaseOrder().getVendor().getAddress()
                    + "\nGST NO:"
                    + invoiceEntity.getPurchaseOrder().getVendor().getGstNumber()
                    + "\nEmail ID:"
                    + invoiceEntity.getPurchaseOrder().getVendor().getEmail()))
        .setBorderBottom(new SolidBorder(1));
  }

  private Div createBillToSection(InvoiceEntity invoiceEntity, PdfFont font) {
    Table table =
        new Table(2)
            .setWidth(UnitValue.createPercentValue(100))
            .setBackgroundColor(LIGHT_BLUE)
            .setBorder(new SolidBorder(1));

    Cell leftCell =
        new Cell()
            .add(
                new Paragraph("Bill To:")
                    .add("\nNAME:" + invoiceEntity.getCustomer().getCustomerName())
                    .add("\nBILLING ADDRESS:" + invoiceEntity.getCustomer().getBillingAddress())
                    .add("\nSHIPPING ADDRESS:" + invoiceEntity.getCustomer().getShippingAddress())
                    .add("\nGST NO:" + invoiceEntity.getCustomer().getGstNumber()))
            .setBorder(Border.NO_BORDER);

    Cell rightCell =
        new Cell()
            .add(new Paragraph("Payment Due Date:" + formatDate(invoiceEntity.getDueDate())))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(Border.NO_BORDER);

    table.addCell(leftCell);
    table.addCell(rightCell);
    return new Div().add(table);
  }

  private Table createItemsTable(InvoiceEntity invoiceEntity, PdfFont font) {
    Table table =
        new Table(new float[] {2, 2, 2, 1, 2, 2, 2})
            .setWidth(UnitValue.createPercentValue(100))
            .setBorder(new SolidBorder(1));

    Stream.of("Item Name", "Description", "HSN Code", "QTY", "Rate", "Tax", "Amount")
        .forEach(
            header ->
                table.addCell(
                    new Cell()
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .add(new Paragraph(header).setFont(font))));

    for (ItemEntity item : invoiceEntity.getPurchaseOrder().getQuote().getItems()) {
      addRow(
          table,
          font,
          item.getItemName(),
          item.getDescription(),
          item.getHsnCode(),
          String.valueOf(item.getQuantity()),
          invoiceEntity.getPurchaseOrder().getQuote().getCurrency() + item.getRate(),
          item.getTaxPercent() + "%",
          invoiceEntity.getPurchaseOrder().getQuote().getCurrency() + item.getTotal());
    }
    return table;
  }

  private Table createTotalAndTermsSection(InvoiceEntity invoiceEntity, PdfFont font) {
    Table table = new Table(2).setWidth(UnitValue.createPercentValue(100));

    double taxPercentage = (invoiceEntity.getTaxAmount() / invoiceEntity.getTotalAmount()) * 100;

    Div totalDiv =
        new Div()
            .add(
                new Paragraph("Tax Percentage: " + String.format("%.2f", taxPercentage) + "%")
                    .setFont(font))
            .add(
                new Paragraph(
                        "Balance Received: "
                            + invoiceEntity.getPurchaseOrder().getQuote().getCurrency()
                            + (invoiceEntity.getTotalAmount() - invoiceEntity.getDueAmount()))
                    .setFont(font))
            .add(
                new Paragraph(
                        "Balance Due: "
                            + invoiceEntity.getPurchaseOrder().getQuote().getCurrency()
                            + invoiceEntity.getDueAmount())
                    .setFont(font))
            .add(
                new Paragraph(
                        "Grand Total: "
                            + invoiceEntity.getPurchaseOrder().getQuote().getCurrency()
                            + invoiceEntity.getTotalAmount())
                    .setBold()
                    .setBackgroundColor(DARK_BLUE)
                    .setFontColor(ColorConstants.WHITE)
                    .setFont(font)
                    .setBorder(new SolidBorder(1)));

    Cell totalCell = new Cell().add(totalDiv).setBorderRight(new SolidBorder(1));

    // Terms & Conditions
    Div termsDiv =
        new Div()
            .setPadding(1)
            .add(new Paragraph("Terms & conditions").setBold())
            .add(
                new Paragraph("1. Payment is due within 30 days from the date of invoice.")
                    .setFont(font))
            .add(
                new Paragraph("2. Goods once sold will not be taken back or exchanged.")
                    .setFont(font))
            .add(
                new Paragraph("3. Delivery shall be made within the agreed timeframe.")
                    .setFont(font));

    Cell termsCell =
        new Cell().add(termsDiv).setBackgroundColor(LIGHT_BLUE).setBorderLeft(new SolidBorder(1));

    table.addCell(termsCell);
    table.addCell(totalCell);
    return table;
  }

  private Div createTotalAmountBox(InvoiceEntity invoiceEntity, PdfFont font) throws IOException {
    return new Div()
        .add(
            new Paragraph(
                    "Total Amount - In Words: "
                        + invoiceEntity.getPurchaseOrder().getQuote().getCurrency()
                        + " "
                        + convertToWords(invoiceEntity.getTotalAmount().intValue()))
                .setFontSize(12)
                .setFont(font))
        .setBorder(new SolidBorder(1))
        .setFont(font)
        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
        .setPadding(10);
  }

  private Table createFooter(PdfFont font) {
    Table footerTable = new Table(2).setWidth(UnitValue.createPercentValue(100));

    Cell businessCell =
        new Cell()
            .add(new Paragraph("For: Business Name").setFontSize(12).setPadding(20))
            .setBorder(Border.NO_BORDER)
            .setFont(font);

    Cell signatureCell =
        new Cell()
            .add(new Paragraph("Authorised Signature").setFontSize(12).setPadding(20))
            .setBorder(Border.NO_BORDER)
            .setFont(font);

    footerTable.addCell(businessCell);
    footerTable.addCell(signatureCell);
    return footerTable;
  }

  //    Helper Methods

  public static PdfFont loadUnicodeFont() {
    try (InputStream is =
        POPdfGeneration.class.getResourceAsStream("/fonts/NotoSans-Regular.ttf")) {
      if (is == null) {
        throw new IOException("Font not found in /fonts/");
      }
      return PdfFontFactory.createFont(is.readAllBytes(), PdfEncodings.IDENTITY_H);
    } catch (IOException e) {
      throw new RuntimeException("Could not load Unicode font", e);
    }
  }

  private void addRow(Table table, PdfFont font, String... cells) {
    for (String cell : cells) {
      table.addCell(new Cell().add(new Paragraph(cell).setFont(font)));
    }
  }

  private String formatDate(LocalDate date) {
    if (date == null) return "";
    return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
  }

  private String convertToWords(int n) {
    if (n == 0) return "Zero";

    int[] values = {
      1000000000,
      1000000,
      1000,
      100,
      90,
      80,
      70,
      60,
      50,
      40,
      30,
      20,
      19,
      18,
      17,
      16,
      15,
      14,
      13,
      12,
      11,
      10,
      9,
      8,
      7,
      6,
      5,
      4,
      3,
      2,
      1
    };

    String[] words = {
      "Billion",
      "Million",
      "Thousand",
      "Hundred",
      "Ninety",
      "Eighty",
      "Seventy",
      "Sixty",
      "Fifty",
      "Forty",
      "Thirty",
      "Twenty",
      "Nineteen",
      "Eighteen",
      "Seventeen",
      "Sixteen",
      "Fifteen",
      "Fourteen",
      "Thirteen",
      "Twelve",
      "Eleven",
      "Ten",
      "Nine",
      "Eight",
      "Seven",
      "Six",
      "Five",
      "Four",
      "Three",
      "Two",
      "One"
    };

    return convertToWordsRec(n, values, words);
  }

  private String convertToWordsRec(int n, int[] values, String[] words) {
    String res = "";

    for (int i = 0; i < values.length; i++) {
      int value = values[i];
      String word = words[i];

      if (n >= value) {

        if (n >= 100) res += convertToWordsRec(n / value, values, words) + " ";

        res += word;

        if (n % value > 0) res += " " + convertToWordsRec(n % value, values, words);

        return res;
      }
    }

    return res;
  }
}
