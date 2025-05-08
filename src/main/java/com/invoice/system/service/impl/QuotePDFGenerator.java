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
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    addSignature(document, font);
    addWatermark(pdf, font);
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

    Table itemTable = new Table(6);
    itemTable.setWidth(500);
    itemTable.setFont(font);
    //    itemTable.setBorder(new SolidBorder(1));
    itemTable.setPadding(1);
    itemTable.setMarginTop(11);
    itemTable.setTextAlignment(TextAlignment.CENTER);

    Stream.of("NAME", "DESCRIPTION", "UNIT COST", "QUANTITY", "Tax", "AMOUNT")
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
              itemTable.addCell(
                  createItemCell(
                      item.getDescription() + "\n(" + item.getHsnCode() + ")", bgColor, font));
              itemTable.addCell(
                  createItemCell(formatCurrency(item.getRate(), quoteEntity), bgColor, font));
              itemTable.addCell(createItemCell(String.valueOf(item.getQuantity()), bgColor, font));
              itemTable.addCell(createItemCell(item.getTaxPercent() + "%", bgColor, font));
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

    document.add(totalParagraph);

    document.add(new AreaBreak());

    document.add(
        new Paragraph("Terms & Conditions")
            .setBold()
            .setFontSize(14)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20));

    Paragraph terms =
        new Paragraph("1. Payment is due within 30 days from the date of invoice")
            .setFontColor(new DeviceRgb(90, 90, 90))
            .setFontSize(11)
            .setTextAlignment(TextAlignment.LEFT)
            .setMarginBottom(20);
    Paragraph terms1 =
        new Paragraph("2. Goods once sold will not be taken back or exchanged")
            .setFontColor(new DeviceRgb(90, 90, 90))
            .setFontSize(11)
            .setTextAlignment(TextAlignment.LEFT)
            .setMarginBottom(20);
    Paragraph terms2 =
        new Paragraph("3. Delivery shall be made within the agreed timeframe.")
            .setFontColor(new DeviceRgb(90, 90, 90))
            .setFontSize(11)
            .setTextAlignment(TextAlignment.LEFT)
            .setMarginBottom(20);

    Div footer =
        new Div()
            .add(terms)
            .add(terms1)
            .add(terms2)
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
      canvas.showText("Quote");
      canvas.endText();

      canvas.restoreState();
    }
  }

  public void addSignature(Document document, PdfFont font) {
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
}
