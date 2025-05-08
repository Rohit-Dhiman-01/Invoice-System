package com.invoice.system.service.impl;

import com.invoice.system.dto.PurchaseOrderResponse;
import com.invoice.system.model.ItemEntity;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class POPdfGeneration {
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

  public void addWatermark(PdfDocument pdfDoc, PurchaseOrderResponse purchaseOrder) {
    PdfFont font = loadUnicodeFont();
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
      canvas.showText("Purchase Order");
      canvas.endText();

      canvas.restoreState();
    }
  }

  void generatePdfContent(Document document, PurchaseOrderResponse purchaseOrder) {
    PdfFont font = loadUnicodeFont();
    document.setMargins(30, 30, 30, 30);
    addCompanyHeader(document, font);
    document.add(new Paragraph("\n").setMarginBottom(5));
    addPurchaseOrderInfo(document, purchaseOrder, font);
    document.add(new Paragraph("\n").setMarginBottom(5));
    addItemsTable(document, purchaseOrder, font);
    document.add(new Paragraph("\n").setMarginBottom(5));
    addTotalsSection(document, purchaseOrder, font);
    document.add(new Paragraph("\n").setMarginBottom(5));
    document.add(new AreaBreak()); // Start new page for Terms & Conditions
    addTermsAndConditions(document, font);
    addFooter(document, font);
  }

  public void addCompanyHeader(Document document, PdfFont font) {
    DeviceRgb primaryColor = new DeviceRgb(25, 55, 95);

    Table headerTable =
        new Table(UnitValue.createPercentArray(new float[] {70, 30}))
            .useAllAvailableWidth()
            .setMarginBottom(10);

    Cell companyCell =
        new Cell()
            .add(
                new Paragraph("YOUR COMPANY NAME")
                    .setFont(font)
                    .setFontSize(18)
                    .setFontColor(primaryColor))
            .add(new Paragraph("123 Business Street").setFontSize(9))
            .add(new Paragraph("City, State, ZIP").setFontSize(9))
            .add(new Paragraph("Phone: 123-456-7890").setFontSize(9))
            .add(new Paragraph("Email: info@yourcompany.com").setFontSize(9))
            .setBorder(Border.NO_BORDER)
            .setPadding(5)
            .setBackgroundColor(new DeviceRgb(245, 245, 245));

    Cell titleCell =
        new Cell()
            .add(
                new Paragraph("PURCHASE ORDER")
                    .setFont(font)
                    .setFontSize(22)
                    .setFontColor(primaryColor))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(Border.NO_BORDER)
            .setPadding(5)
            .setBackgroundColor(new DeviceRgb(245, 245, 245));

    headerTable.addCell(companyCell);
    headerTable.addCell(titleCell);

    document.add(headerTable);

    SolidLine line = new SolidLine(1f);
    line.setColor(primaryColor);
    LineSeparator ls = new LineSeparator(line);
    document.add(ls);
  }

  public void addPurchaseOrderInfo(
      Document document, PurchaseOrderResponse purchaseOrder, PdfFont font) {
    DeviceRgb primaryColor = new DeviceRgb(25, 55, 95);

    Table infoTable =
        new Table(UnitValue.createPercentArray(new float[] {50, 50}))
            .useAllAvailableWidth()
            .setMarginTop(8)
            .setMarginBottom(8);

    Table leftColumn =
        new Table(UnitValue.createPercentArray(new float[] {100})).useAllAvailableWidth();

    Cell poDetailsHeader =
        new Cell()
            .add(new Paragraph("PO DETAILS").setFont(font).setBold().setFontSize(10))
            .setBackgroundColor(new DeviceRgb(230, 230, 230))
            .setBorder(Border.NO_BORDER)
            .setPadding(5);

    leftColumn.addCell(poDetailsHeader);

    Table poDetailsTable =
        new Table(UnitValue.createPercentArray(new float[] {40, 60})).useAllAvailableWidth();

    poDetailsTable.addCell(createDetailLabelCell("PO Number:", font));
    poDetailsTable.addCell(createDetailValueCell(purchaseOrder.getPoNumber(), true, font));

    poDetailsTable.addCell(createDetailLabelCell("PO Date:", font));
    poDetailsTable.addCell(
        createDetailValueCell(String.valueOf(purchaseOrder.getPoDate()), false, font));
    leftColumn.addCell(new Cell().add(poDetailsTable).setBorder(Border.NO_BORDER));

    Table rightColumn =
        new Table(UnitValue.createPercentArray(new float[] {100})).useAllAvailableWidth();

    Cell shippingHeader =
        new Cell()
            .add(new Paragraph("SHIPPING ADDRESS").setFont(font).setBold().setFontSize(10))
            .setBackgroundColor(new DeviceRgb(230, 230, 230))
            .setBorder(Border.NO_BORDER)
            .setPadding(5);

    Cell shippingAddress =
        new Cell()
            .add(new Paragraph(purchaseOrder.getShippingAddress()).setFont(font))
            .setPadding(6)
            .setBorder(new SolidBorder(new DeviceRgb(200, 200, 200), 1))
            .setHeight(50f);

    rightColumn.addCell(shippingHeader);
    rightColumn.addCell(shippingAddress);

    infoTable.addCell(new Cell().add(leftColumn).setBorder(Border.NO_BORDER));
    infoTable.addCell(new Cell().add(rightColumn).setBorder(Border.NO_BORDER));

    document.add(infoTable);
  }

  public Cell createDetailLabelCell(String text, PdfFont font) {
    return new Cell()
        .add(new Paragraph(text).setFont(font).setBold().setFontSize(9))
        .setBorder(Border.NO_BORDER)
        .setPadding(4);
  }

  public Cell createDetailValueCell(String text, boolean bold, PdfFont font) {
    Paragraph p = new Paragraph(text).setFont(font).setFontSize(9);
    if (bold) p.setBold();
    return new Cell().add(p).setBorder(Border.NO_BORDER).setPadding(4);
  }

  public void addItemsTable(Document document, PurchaseOrderResponse purchaseOrder, PdfFont font) {
    DeviceRgb primaryColor = new DeviceRgb(25, 55, 95);

    Table itemsTable =
        new Table(UnitValue.createPercentArray(new float[] {5, 15, 32, 7, 8, 8, 10}))
            .useAllAvailableWidth()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(7)
            .setMarginBottom(7)
            .setBorder(new SolidBorder(new DeviceRgb(200, 200, 200), 0.5f));

    itemsTable.addHeaderCell(createHeaderCell("No.", primaryColor, font));
    itemsTable.addHeaderCell(createHeaderCell("Item Name", primaryColor, font));
    itemsTable.addHeaderCell(createHeaderCell("Description", primaryColor, font));
    itemsTable.addHeaderCell(createHeaderCell("Qty", primaryColor, font));
    itemsTable.addHeaderCell(createHeaderCell("Rate", primaryColor, font));
    //    itemsTable.addHeaderCell(createHeaderCell("HSN Code", primaryColor, font));
    itemsTable.addHeaderCell(createHeaderCell("Tax %", primaryColor, font));
    itemsTable.addHeaderCell(createHeaderCell("Total", primaryColor, font));

    int index = 1;
    boolean alternateRow = false;
    for (ItemEntity item : purchaseOrder.getItems()) {
      itemsTable.addCell(createCell(String.valueOf(index++), alternateRow, font));
      itemsTable.addCell(createCell(item.getItemName(), alternateRow, font));
      itemsTable.addCell(
          createCell(item.getDescription() + "\n(" + item.getHsnCode() + ")", alternateRow, font));
      itemsTable.addCell(createCell(String.valueOf(item.getQuantity()), alternateRow, font, true));
      itemsTable.addCell(
          createCell(
              purchaseOrder.getCurrency() + DECIMAL_FORMAT.format(item.getRate()),
              alternateRow,
              font));
      //      itemsTable.addCell(createCell(item.getHsnCode(), alternateRow, font));
      itemsTable.addCell(createCell(item.getTaxPercent() + "%", alternateRow, font));
      itemsTable.addCell(
          createCell(
                  purchaseOrder.getCurrency() + DECIMAL_FORMAT.format(item.getTotal()),
                  alternateRow,
                  font)
              .setBold());

      alternateRow = !alternateRow;
    }

    document.add(itemsTable);
  }

  public Cell createHeaderCell(String text, DeviceRgb headerColor, PdfFont font) {
    return new Cell()
        .add(
            new Paragraph(text)
                .setFont(font)
                .setBold()
                .setFontColor(ColorConstants.WHITE)
                .setFontSize(9))
        .setBackgroundColor(headerColor)
        .setPadding(5)
        .setTextAlignment(TextAlignment.CENTER);
  }

  public Cell createCell(String text, boolean alternate, PdfFont font, boolean isQty) {
    Paragraph p = new Paragraph(text).setFont(font).setFontSize(isQty ? 8 : 9);
    if (isQty) {
      p.setHyphenation(null); // Disable hyphenation to prevent line breaks
      p.setTextAlignment(TextAlignment.CENTER);
    }

    Cell cell = new Cell().add(p).setPadding(4).setTextAlignment(TextAlignment.CENTER);

    if (alternate) {
      cell.setBackgroundColor(new DeviceRgb(250, 250, 250));
    }

    return cell;
  }

  public Cell createCell(String text, boolean alternate, PdfFont font) {
    return createCell(text, alternate, font, false);
  }

  public void addFooter(Document document, PdfFont font) {
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

  public void addTotalsSection(
      Document document, PurchaseOrderResponse purchaseOrder, PdfFont font) {
    DeviceRgb primaryColor = new DeviceRgb(25, 55, 95);

    Table summaryLayout =
        new Table(UnitValue.createPercentArray(new float[] {100}))
            .useAllAvailableWidth()
            .setMarginTop(8);

    Table totalsTable =
        new Table(UnitValue.createPercentArray(new float[] {60, 40}))
            .useAllAvailableWidth()
            .setMarginLeft(15);

    totalsTable.addCell(
        new Cell()
            .add(new Paragraph("Subtotal").setFont(font).setBold().setFontSize(9))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(Border.NO_BORDER)
            .setPadding(3));
    totalsTable.addCell(
        new Cell()
            .add(
                new Paragraph(
                        purchaseOrder.getCurrency()
                            + DECIMAL_FORMAT.format(purchaseOrder.getSubTotal()))
                    .setFont(font)
                    .setFontSize(9))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(Border.NO_BORDER)
            .setPadding(3));

    totalsTable.addCell(
        new Cell()
            .add(new Paragraph("Tax Amount").setFont(font).setBold().setFontSize(9))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(Border.NO_BORDER)
            .setPadding(3));
    totalsTable.addCell(
        new Cell()
            .add(
                new Paragraph(
                        purchaseOrder.getCurrency()
                            + DECIMAL_FORMAT.format(purchaseOrder.getTaxAmount()))
                    .setFont(font)
                    .setFontSize(9))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(Border.NO_BORDER)
            .setPadding(3));

    DeviceRgb grayBackground = new DeviceRgb(230, 230, 230);
    DeviceRgb lineColor = new DeviceRgb(50, 50, 50); // or your theme color

    // Left cell (no background, no line)
    Cell totalLabel =
        new Cell()
            .add(new Paragraph("Total Amount").setFont(font).setBold().setFontSize(10))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(Border.NO_BORDER)
            .setPadding(4);

    // Right cell (background + top border only)
    Cell totalValue =
        new Cell()
            .add(
                new Paragraph(
                        purchaseOrder.getCurrency()
                            + DECIMAL_FORMAT.format(purchaseOrder.getTotalAmount()))
                    .setFont(font)
                    .setBold()
                    .setFontSize(10))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBackgroundColor(grayBackground)
            .setBorderTop(new SolidBorder(lineColor, 1f)) // line only at top of this cell
            .setBorderBottom(Border.NO_BORDER)
            .setBorderLeft(Border.NO_BORDER)
            .setBorderRight(Border.NO_BORDER)
            .setPadding(4);

    // Add cells to the totals table
    totalsTable.addCell(totalLabel);
    totalsTable.addCell(totalValue);

    summaryLayout.addCell(new Cell().add(totalsTable).setBorder(Border.NO_BORDER));
    document.add(summaryLayout);
  }

  public void addTermsAndConditions(Document document, PdfFont font) {
    Paragraph heading =
        new Paragraph("Terms and Conditions:")
            .setFont(font)
            .setBold()
            .setFontSize(10)
            .setMarginBottom(10);

    Paragraph term1 =
        new Paragraph("1. Payment is due within 30 days from the date of invoice.")
            .setFont(font)
            .setFontSize(9)
            .setMarginBottom(5);
    Paragraph term2 =
        new Paragraph("2. Goods once sold will not be taken back or exchanged.")
            .setFont(font)
            .setFontSize(9)
            .setMarginBottom(5);
    Paragraph term3 =
        new Paragraph("3. Delivery shall be made within the agreed timeframe.")
            .setFont(font)
            .setFontSize(9)
            .setMarginBottom(5);

    document.add(heading);
    document.add(term1);
    document.add(term2);
    document.add(term3);
  }

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
}
