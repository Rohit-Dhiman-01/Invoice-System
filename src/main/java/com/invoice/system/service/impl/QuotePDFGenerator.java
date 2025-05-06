package com.invoice.system.service.impl;

import com.invoice.system.model.QuoteEntity;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import lombok.SneakyThrows;

public class QuotePDFGenerator {

  @SneakyThrows
  public ByteArrayInputStream generateQuotePdf(QuoteEntity quoteEntity) {
    PdfFont font = loadUnicodeFont();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
    Document document = new Document(pdf);

    addHeader(document, quoteEntity, font);
    addBillingInfo(document, quoteEntity, font);
    addItemsTable(document, quoteEntity, font);
    addFooter(document, quoteEntity, font);
    document.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private void addHeader(Document document, QuoteEntity quoteEntity, PdfFont font)
      throws IOException {
    Table headerTable = new Table(2);
    headerTable.setWidth(500);
    headerTable.setMarginTop(20);

    Cell titleCell =
        new Cell()
            .add(
                new Paragraph("QUOTE")
                    .setFontColor(new DeviceRgb(143, 141, 141))
                    .setFontSize(25)
                    .setTextAlignment(TextAlignment.CENTER))
            .setBorder(Border.NO_BORDER);
    Cell logoCell =
        new Cell()
            .add(addLogo(document.getPdfDocument()))
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT);
    headerTable.addCell(titleCell);
    headerTable.addCell(logoCell);
    document.add(headerTable);
    Div detailsContainer =
        new Div().setTextAlignment(TextAlignment.LEFT).setMarginTop(10).setMarginBottom(20);
    document.add(detailsContainer);

    Div headerContainer = new Div().setTextAlignment(TextAlignment.LEFT).setMarginBottom(20);

    Paragraph numberParagraph =
        new Paragraph()
            .add(new Text("QUOTE NUMBER: ").setFontColor(new DeviceRgb(90, 90, 90)).setFont(font))
            .add(new Text(quoteEntity.getQuoteNumber()))
            .setMarginBottom(5);

    Paragraph dateParagraph =
        new Paragraph()
            .add(new Text("QUOTE DATE: ").setFontColor(new DeviceRgb(90, 90, 90)).setFont(font))
            .add(new Text(String.valueOf(quoteEntity.getQuoteDate())));

    headerContainer.add(numberParagraph);
    headerContainer.add(dateParagraph);
    document.add(headerContainer);
  }

  private void addBillingInfo(Document document, QuoteEntity quoteEntity, PdfFont font) {
    Table billingTable = new Table(2);
    billingTable.setWidth(500);

    Cell billedToCell =
        new Cell()
            .add(new Paragraph("BILLED TO").setFontColor(new DeviceRgb(90, 90, 90)).setFont(font))
            .add(new Paragraph(quoteEntity.getCustomer().getCustomerName()))
            .add(new Paragraph(quoteEntity.getCustomer().getBillingAddress()))
            .setBorder(Border.NO_BORDER);

    Cell yourCompanyCell =
        new Cell()
            .add(
                new Paragraph("YOUR COMPANY").setFontColor(new DeviceRgb(90, 90, 90)).setFont(font))
            .add(new Paragraph("Building name"))
            .add(new Paragraph("123 Your Street"))
            .setBorder(Border.NO_BORDER);

    billingTable.addCell(billedToCell);
    billingTable.addCell(yourCompanyCell);
    document.add(billingTable);
  }

  private void addItemsTable(Document document, QuoteEntity quoteEntity, PdfFont font) {
    DeviceRgb headerColor = new DeviceRgb(90, 90, 90);
    DeviceRgb bgColor = new DeviceRgb(236, 236, 236);
    Border bottomBorder = new SolidBorder(1);

    Table itemTable = new Table(5);
    itemTable.setWidth(500);
    itemTable.setFont(font);
    itemTable.setBorder(Border.NO_BORDER);
    itemTable.setMarginTop(11);

    Stream.of("NAME", "DESCRIPTION", "UNIT COST", "QTY/HR RATE", "AMOUNT")
        .forEach(
            header ->
                itemTable.addHeaderCell(
                    new Cell()
                        .add(new Paragraph(header).setFontColor(headerColor).setFont(font))
                        .setBackgroundColor(bgColor)
                        .setBorderBottom(bottomBorder)
                        .setBorder(Border.NO_BORDER)));

    quoteEntity
        .getItems()
        .forEach(
            item -> {
              itemTable.addCell(createItemCell(item.getItemName(), bgColor, font));
              itemTable.addCell(createItemCell(item.getDescription(), bgColor, font));
              itemTable.addCell(
                  createItemCell(formatCurrency(item.getRate(), quoteEntity), bgColor, font));
              itemTable.addCell(createItemCell(String.valueOf(item.getQuantity()), bgColor, font));
              itemTable.addCell(
                  createItemCell(formatCurrency(item.getTotal(), quoteEntity), bgColor, font));
            });

    itemTable.setBorderBottom(bottomBorder);
    document.add(itemTable);

    Table totalsTable = new Table(2);
    totalsTable.setWidth(500);
    totalsTable.setBackgroundColor(bgColor);
    totalsTable.setBorder(Border.NO_BORDER);

    Cell totalsContent =
        new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPadding(10);

    totalsContent.add(
        new Paragraph()
            .add(new Text("SUBTOTAL: ").setFontColor(headerColor).setFont(font))
            .add(new Text(formatCurrency(quoteEntity.getSubTotal(), quoteEntity)).setFont(font)));
    totalsContent.add(
        new Paragraph()
            .add(new Text("TAX: ").setFontColor(headerColor).setFont(font))
            .add(new Text(formatCurrency(quoteEntity.getTaxAmount(), quoteEntity)).setFont(font)));

    totalsContent.add(
        new Paragraph()
            .add(new Text("TOTAL: ").setFontColor(headerColor).setFont(font))
            .add(
                new Text(
                    formatCurrency(
                        quoteEntity.getSubTotal() + quoteEntity.getTaxAmount(), quoteEntity)))
            .setBold()
            .setFont(font));

    totalsTable.addCell(totalsContent);
    totalsTable.addCell(new Cell().setBorder(Border.NO_BORDER));

    document.add(totalsTable);
  }

  private void addFooter(Document document, QuoteEntity quoteEntity, PdfFont font) {

    Paragraph totalParagraph =
        new Paragraph()
            .add(
                new Text("TOTAL\n")
                    .setFontColor(new DeviceRgb(90, 90, 90))
                    .setFont(font)
                    .setFontSize(13))
            .add(
                new Text(
                        formatCurrency(
                            quoteEntity.getSubTotal() + quoteEntity.getTaxAmount(), quoteEntity))
                    .setFontColor(DeviceRgb.BLACK)
                    .setFontSize(21)
                    .setFont(font))
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(20)
            .setMarginRight(30)
            .setMarginBottom(10);

    Paragraph terms =
        new Paragraph("Terms: Payment due within 30 days")
            .setFontColor(new DeviceRgb(90, 90, 90))
            .setFontSize(11)
            .setTextAlignment(TextAlignment.LEFT)
            .setMarginBottom(20);

    Div footer =
        new Div()
            .add(totalParagraph)
            .add(terms)
            .setWidth(500)
            .setHorizontalAlignment(HorizontalAlignment.RIGHT);

    document.add(footer);
  }

  private Image addLogo(PdfDocument pdfDocument) throws IOException {
    try {
      ImageData imageData =
          ImageDataFactory.create("C:\\Users\\HP\\Downloads\\7a3ec529632909.55fc107b84b8c.png");
      Image logo = new Image(imageData);
      logo.setAutoScale(true);
      logo.setHeight(15);
      return logo;
    } catch (Exception e) {
      PdfFormXObject xObject = new PdfFormXObject(new Rectangle(100, 30));
      new PdfCanvas(xObject, pdfDocument)
          .beginText()
          .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12)
          .moveText(5, 5)
          .showText("[YOUR LOGO]")
          .endText();
      return new Image(xObject);
    }
  }

  // Helper Function
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

  private String formatCurrency(double amount, QuoteEntity quoteEntity) {

    return String.format("%s%.2f", quoteEntity.getCurrency(), amount);
  }

  private Cell createItemCell(String content, DeviceRgb bgColor, PdfFont font) {
    return new Cell()
        .add(new Paragraph(content).setFont(font))
        .setBackgroundColor(bgColor)
        .setBorder(Border.NO_BORDER)
        .setPadding(5);
  }
}
