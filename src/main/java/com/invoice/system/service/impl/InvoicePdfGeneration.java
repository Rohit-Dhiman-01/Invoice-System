package com.invoice.system.service.impl;

import com.invoice.system.model.InvoiceEntity;
import com.invoice.system.model.ItemEntity;
import com.itextpdf.io.font.constants.StandardFonts;
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
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
public class InvoicePdfGeneration {
    private static final DeviceRgb DARK_BLUE = new DeviceRgb(7, 23, 123);
    private static final DeviceRgb LIGHT_BLUE = new DeviceRgb(173, 216, 230);
    private static final DeviceRgb BLACK = new DeviceRgb(0, 0, 0);


    @SneakyThrows
    public ByteArrayInputStream generateInvoicePdf(InvoiceEntity invoiceEntity) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        Div mainContainer = new Div()
                .setBorder(new SolidBorder(BLACK, 3));

        mainContainer.add(createHeader(invoiceEntity));

        mainContainer.add(createCompanyDetails(invoiceEntity).setMarginTop(0));

        mainContainer.add(createBillToSection(invoiceEntity).setMarginTop(0));

        mainContainer.add(createItemsTable(invoiceEntity).setMarginTop(0));

        mainContainer.add(createTotalAndTermsSection(invoiceEntity));

        mainContainer.add(createTotalAmountBox(invoiceEntity));

        mainContainer.add(createFooter());

        document.add(mainContainer);
        document.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public void savePdfToFile(byte[] pdfBytes, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
        }
    }

    private Table createHeader(InvoiceEntity invoiceEntity) throws IOException {
        Table headerTable = new Table(2)
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(DARK_BLUE);

        Cell leftCell = new Cell()
                .add(new Paragraph("TAX INVOICE")
                        .setFontColor(ColorConstants.WHITE)
                        .setFontSize(20)
                        .setPadding(5)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);

        Cell rightCell = new Cell()
                .add(new Paragraph("INVOICE NO:" + invoiceEntity.getInvoiceNumber()
                        + "\nDATE: " + formatDate(invoiceEntity.getInvoiceDate()))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFontColor(ColorConstants.WHITE))
                .setBorder(Border.NO_BORDER);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);
        return headerTable;
    }

    private Div createCompanyDetails(InvoiceEntity invoiceEntity) {
        return new Div()
                .setBorder(new SolidBorder(1))
                .setPadding(1)
                .add(new Paragraph(
                        "COMPANY NAME:" + invoiceEntity.getPurchaseOrder().getVendor().getVendorName() +
                                "\nADDRESS:" + invoiceEntity.getPurchaseOrder().getVendor().getAddress() +
                                "\nGST NO:" + invoiceEntity.getPurchaseOrder().getVendor().getGstNumber() +
                                "\nEmail ID:" + invoiceEntity.getPurchaseOrder().getVendor().getEmail()))
                .setBorderBottom(new SolidBorder(1));
    }

    private Div createBillToSection(InvoiceEntity invoiceEntity) {
        Table table = new Table(2)
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(LIGHT_BLUE)
                .setBorder(new SolidBorder(1));

        Cell leftCell = new Cell()
                .add(new Paragraph("Bill To:")
                        .add("\nNAME:" + invoiceEntity.getCustomer().getCustomerName())
                        .add("\nBILLING ADDRESS:" + invoiceEntity.getCustomer().getBillingAddress())
                        .add("\nSHIPPING ADDRESS:" + invoiceEntity.getCustomer().getShippingAddress())
                        .add("\nGST NO:" + invoiceEntity.getCustomer().getGstNumber()))
                .setBorder(Border.NO_BORDER);

        Cell rightCell = new Cell()
                .add(new Paragraph("Payment Due Date:" + formatDate(invoiceEntity.getDueDate())))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);

        table.addCell(leftCell);
        table.addCell(rightCell);
        return new Div().add(table);
    }

    private Table createItemsTable(InvoiceEntity invoiceEntity) throws IOException {
        Table table = new Table(new float[]{3, 2, 2, 2, 2})
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(1));

        Stream.of("Description", "HSN Code", "Qty", "Rate", "Amount")
                .forEach(header -> table.addCell(
                        new Cell()
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .add(new Paragraph(header))));


        for (ItemEntity item : invoiceEntity.getPurchaseOrder().getQuote().getItems()) {
            addRow(table, item.getDescription(), item.getHsnCode(),
                    String.valueOf(item.getQuantity()), String.valueOf(item.getRate()), String.valueOf(item.getTotal()));
        }
        return table;
    }

    private Table createTotalAndTermsSection(InvoiceEntity invoiceEntity) throws IOException {
        Table table = new Table(2)
                .setWidth(UnitValue.createPercentValue(100));

        double taxPercentage = (invoiceEntity.getTaxAmount() / invoiceEntity.getTotalAmount()) * 100;

        Div totalDiv = new Div()
                .add(new Paragraph("Tax Percentage: " + String.format("%.2f", taxPercentage) + "%"))
                .add(new Paragraph("Balance Received: 0.0"))
                .add(new Paragraph("Balance Due: 0.0"))
                .add(new Paragraph("Grand Total: " + invoiceEntity.getTotalAmount()).setBold()
                        .setBackgroundColor(DARK_BLUE)
                        .setFontColor(ColorConstants.WHITE)
                        .setBorder(new SolidBorder(1)));

        Cell totalCell = new Cell()
                .add(totalDiv)
                .setBorderRight(new SolidBorder(1));

        // Terms & Conditions
        Div termsDiv = new Div()
                .setPadding(5)
                .add(new Paragraph("Terms & conditions").setBold())
                .add(new Paragraph("1. "))
                .add(new Paragraph("2. "))
                .add(new Paragraph("3. "))
                .add(new Paragraph("4. "));

        Cell termsCell = new Cell()
                .add(termsDiv)
                .setBackgroundColor(LIGHT_BLUE)
                .setBorderLeft(new SolidBorder(1));

        table.addCell(termsCell);
        table.addCell(totalCell);
        return table;
    }

    private Div createTotalAmountBox(InvoiceEntity invoiceEntity) throws IOException {
        return new Div()
                .add(new Paragraph("Total Amount $ - In Words): " + convertToWords(invoiceEntity.getTotalAmount().intValue()))
                        .setFont(createBoldFont())
                        .setFontSize(12)
                )
                .setBorder(new SolidBorder(1))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(10);
    }

    private Table createFooter() throws IOException {
        Table footerTable = new Table(2)
                .setWidth(UnitValue.createPercentValue(100));

        Cell businessCell = new Cell()
                .add(new Paragraph("For: Business Name")
                        .setFontSize(12)
                        .setPadding(20))
                .setBorder(Border.NO_BORDER);

        Cell signatureCell = new Cell()
                .add(new Paragraph("Authorised Signature")
                        .setFontSize(12)
                        .setPadding(20))
                .setBorder(Border.NO_BORDER);

        footerTable.addCell(businessCell);
        footerTable.addCell(signatureCell);
        return footerTable;
    }

//    Helper Methods

    private void addRow(Table table, String... cells) {
        for (String cell : cells) {
            table.addCell(new Cell().add(new Paragraph(cell)));
        }
    }

    private PdfFont createBoldFont() throws IOException {
        return PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private String convertToWords(int n) {
        if (n == 0)
            return "Zero";

        // Key Numeric values and their corresponding English words
        int[] values = {
                1000000000, 1000000, 1000, 100, 90, 80, 70,
                60, 50, 40, 30, 20, 19, 18, 17, 16, 15, 14,
                13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1
        };

        String[] words = {
                "Billion", "Million", "Thousand", "Hundred",
                "Ninety", "Eighty", "Seventy", "Sixty", "Fifty",
                "Forty", "Thirty", "Twenty", "Nineteen",
                "Eighteen", "Seventeen", "Sixteen", "Fifteen",
                "Fourteen", "Thirteen", "Twelve", "Eleven",
                "Ten", "Nine", "Eight", "Seven", "Six", "Five",
                "Four", "Three", "Two", "One"
        };

        return convertToWordsRec(n, values, words);
    }

    private String convertToWordsRec(int n, int[] values, String[] words) {
        String res = "";

        // Iterating over all key Numeric values
        for (int i = 0; i < values.length; i++) {
            int value = values[i];
            String word = words[i];

            // If the number is greater than or equal to current numeric value
            if (n >= value) {

                // Append the quotient part
                // If the number is greater than or equal to 100
                // then only we need to handle that
                if (n >= 100)
                    res += convertToWordsRec(n / value, values, words) + " ";

                // Append the word for numeric value
                res += word;

                // Append the remainder part
                if (n % value > 0)
                    res += " " + convertToWordsRec(n % value, values, words);

                return res;
            }
        }

        return res;
    }

}