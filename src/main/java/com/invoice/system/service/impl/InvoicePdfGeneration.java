package com.invoice.system.service.impl;

import com.invoice.system.model.InvoiceEntity;
import com.invoice.system.model.ItemEntity;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    mainContainer.add(createTotalAmountAndFooterSection(invoiceEntity, font));

    document.add(mainContainer);

    addTermsAndConditionsPage(document, font);
    addWatermark(pdf, font);

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
        new Table(new float[] {2, 2, 1, 2, 2, 2})
            .setWidth(UnitValue.createPercentValue(100))
            .setBorder(new SolidBorder(1))
            .setTextAlignment(TextAlignment.CENTER);

    Stream.of("Item Name", "Description", "QTY", "Rate", "Tax", "Amount")
        .forEach(
            header ->
                table.addCell(
                    new Cell()
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .add(new Paragraph(header).setFont(font))
                        .setTextAlignment(TextAlignment.CENTER)));

    for (ItemEntity item : invoiceEntity.getPurchaseOrder().getQuote().getItems()) {
      addRow(
          table,
          font,
          item.getItemName(),
          item.getDescription() + "  (" + item.getHsnCode() + ")",
          String.valueOf(item.getQuantity()),
          invoiceEntity.getPurchaseOrder().getQuote().getCurrency() + item.getRate(),
          item.getTaxPercent() + "%",
          invoiceEntity.getPurchaseOrder().getQuote().getCurrency() + item.getTotal());
    }
    return table;
  }

  private Table createTotalAndTermsSection(InvoiceEntity invoiceEntity, PdfFont font) {
    Table table =
        new Table(new float[] {2, 2})
            .setWidth(UnitValue.createPercentValue(100)); // 2 equal columns

    double taxPercentage = (invoiceEntity.getTaxAmount() / invoiceEntity.getTotalAmount()) * 100;

    addLabelValueRow(table, font, "Status", String.valueOf(invoiceEntity.getPaymentStatus()));
    addLabelValueRow(table, font, "Tax Percentage", String.format("%.2f", taxPercentage) + "%");
    addLabelValueRow(
        table,
        font,
        "Balance Received",
        invoiceEntity.getPurchaseOrder().getQuote().getCurrency()
            + String.format(
                "%.2f", (invoiceEntity.getTotalAmount() - invoiceEntity.getDueAmount())));
    addLabelValueRow(
        table,
        font,
        "Balance Due",
        invoiceEntity.getPurchaseOrder().getQuote().getCurrency()
            + String.format("%.2f", invoiceEntity.getDueAmount()));

    // Grand Total row (styled)
    Cell grandTotalLabel =
        new Cell()
            .add(new Paragraph("Grand Total").setFont(font).setBold())
            .setBackgroundColor(DARK_BLUE)
            .setFontColor(ColorConstants.WHITE)
            .setBorder(new SolidBorder(1));

    Cell grandTotalValue =
        new Cell()
            .add(
                new Paragraph(
                        invoiceEntity.getPurchaseOrder().getQuote().getCurrency()
                            + String.format("%.2f", invoiceEntity.getTotalAmount()))
                    .setFont(font)
                    .setBold())
            .setBackgroundColor(DARK_BLUE)
            .setFontColor(ColorConstants.WHITE)
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(new SolidBorder(1));

    table.addCell(grandTotalLabel);
    table.addCell(grandTotalValue);

    return table;
  }

  private void addLabelValueRow(Table table, PdfFont font, String label, String value) {
    Cell labelCell =
        new Cell().add(new Paragraph(label).setFont(font)).setBorder(new SolidBorder(1));

    Cell valueCell =
        new Cell()
            .add(new Paragraph(value).setFont(font))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(new SolidBorder(1));

    table.addCell(labelCell);
    table.addCell(valueCell);
  }

  private Table createTotalAmountAndFooterSection(InvoiceEntity invoiceEntity, PdfFont font)
      throws IOException {

    Table outerTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
    Div totalAmountDiv =
        new Div()
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

    Cell totalAmountCell =
        new Cell().add(totalAmountDiv).setBorder(Border.NO_BORDER); // no extra border outside div

    outerTable.addCell(totalAmountCell);

    return outerTable;
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
      table.addCell(new Cell().add(new Paragraph(cell).setFont(font))).setPadding(1);
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

  private void addTermsAndConditionsPage(Document document, PdfFont font) {
    document.add(new AreaBreak()); // Adds a new page

    document.add(
        new Paragraph("Terms & Conditions")
            .setBold()
            .setFontSize(14)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20));

    document.add(
        new Paragraph("1. Payment is due within 30 days from the date of invoice.").setFont(font));
    document.add(
        new Paragraph("2. Goods once sold will not be taken back or exchanged.").setFont(font));
    document.add(
        new Paragraph("3. Delivery shall be made within the agreed timeframe.").setFont(font));

    DeviceRgb primaryColor = new DeviceRgb(25, 55, 95);

    document.add(new Paragraph("\n").setMarginBottom(5));

    SolidLine line = new SolidLine(0.5f);
    line.setColor(new DeviceRgb(200, 200, 200));
    LineSeparator ls = new LineSeparator(line);
    document.add(ls);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Paragraph signatureLine =
        new Paragraph("Authorized Signature: _________________________")
            .setFont(font)
            .setMarginTop(10)
            .setFontSize(9);
    document.add(signatureLine);

    Table footerTable =
        new Table(UnitValue.createPercentArray(new float[] {50, 50}))
            .useAllAvailableWidth()
            .setMarginTop(8);

    Cell dateCell =
        new Cell()
            .add(
                new Paragraph("Generated on: " + dateFormat.format(new Date()))
                    .setFont(font)
                    .setFontSize(8))
            .setBorder(Border.NO_BORDER);

    Cell companyCell =
        new Cell()
            .add(
                new Paragraph("PIXIVERSE")
                    .setFont(font)
                    .setBold()
                    .setFontSize(10)
                    .setFontColor(primaryColor))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(Border.NO_BORDER);

    footerTable.addCell(dateCell);
    footerTable.addCell(companyCell);

    document.add(footerTable);
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

  public void addWatermark(PdfDocument pdfDoc, PdfFont font) {
    int pageCount = pdfDoc.getNumberOfPages();

    for (int i = 1; i <= pageCount; i++) {
      PdfPage page = pdfDoc.getPage(i);
      PdfCanvas canvas = new PdfCanvas(page);

      float pageWidth = page.getPageSize().getWidth();
      float pageHeight = page.getPageSize().getHeight();

      canvas.saveState();
      PdfExtGState gs1 = new PdfExtGState().setFillOpacity(0.08f);
      canvas.setExtGState(gs1);

      float textSize = 40;
      canvas.setFontAndSize(font, textSize);
      canvas.setFillColor(new DeviceRgb(150, 150, 150));

      float centerX = pageWidth / 2;
      float centerY = pageHeight / 2;

      canvas.beginText();
      canvas.setTextMatrix(
          (float) Math.cos(Math.toRadians(45)),
          (float) Math.sin(Math.toRadians(45)),
          (float) -Math.sin(Math.toRadians(45)),
          (float) Math.cos(Math.toRadians(45)),
          centerX - (textSize * 2.2f),
          centerY);
      canvas.showText("Invoice");
      canvas.endText();

      canvas.restoreState();
    }
  }
}
