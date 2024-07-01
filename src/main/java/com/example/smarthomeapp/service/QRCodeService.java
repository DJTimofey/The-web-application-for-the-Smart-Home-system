package com.example.smarthomeapp.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class QRCodeService {

    @Autowired
    private JavaMailSender emailSender;

    // Метод для создания и отправки QR-кода
    public void generateAndSendQRCode(String friendEmail, String friendData, Date expirationDate) {
        try {
            if (new Date().before(expirationDate)) {
                // Check if the current date is before the expiration date

                // Build the URL for redirection using UriComponentsBuilder
                String redirectUrl = "https://172.16.99.102:8443/qr-redirect";
                String encodedRedirectUrl = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8.toString());

                String qrCodeData ="https://172.16.99.102:8443/qr-redirect";


                // Generate QR code and send email
                String base64Image = generateQRCode(qrCodeData);
                sendEmailWithQRCode(friendEmail, base64Image);
            } else {
                System.out.println("QR Code has expired. Not sending the QR Code.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для создания QR-кода
    private String generateQRCode(String userData) throws Exception {
        int width = 300;
        int height = 300;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(userData, BarcodeFormat.QR_CODE, width, height);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    // Метод для отправки письма с вложенным QR-кодом
    private void sendEmailWithQRCode(String recipientEmail, String base64Image) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setSubject("QR Code Pass");

        // Вложение QR-кода в письмо
        helper.setText("<html><body><img src='data:image/png;base64," + base64Image + "' /></body></html>", true);

        emailSender.send(message);

        System.out.println("QR Code sent to: " + recipientEmail);
    }
}
