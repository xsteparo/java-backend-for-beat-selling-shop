package com.cz.cvut.fel.instumentalshop.util.pdf;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

public class PdfGenerator {

    public static byte[] generate(String buyerName, String trackName, LicenceType licenceType,
                                  LocalDateTime purchaseDate, LocalDateTime expiredDate) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.setMargins(120, 36, 36, 36); // top, right, bottom, left


            String logoPath = PdfGenerator.class.getClassLoader().getResource("logo.png").getPath();
            ImageData imageData = ImageDataFactory.create(logoPath);
            Image logo = new Image(imageData)
                    .scaleToFit(100, 100)
                    .setFixedPosition(40, 750);

            document.add(logo);
            document.add(new Paragraph("Licence Certificate").setBold().setFontSize(20));
            document.add(new Paragraph("Buyer: " + buyerName));
            document.add(new Paragraph("Track: " + trackName));
            document.add(new Paragraph("Licence Type: " + licenceType));
            document.add(new Paragraph("Purchase Date: " + purchaseDate));
            document.add(new Paragraph("Valid Until: " + (expiredDate != null ? expiredDate : "Unlimited")));

            document.add(new Paragraph("\n\nSigned by BeatPlatform"));

            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
