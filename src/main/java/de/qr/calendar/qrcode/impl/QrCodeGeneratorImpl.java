package de.qr.calendar.qrcode.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import de.qr.calendar.exception.StorageException;
import de.qr.calendar.file.FileStorageProperties;
import de.qr.calendar.qrcode.QrCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class QrCodeGeneratorImpl implements QrCodeGenerator {

    private QRCodeWriter qrCodeWriter;
    private Map<EncodeHintType, ErrorCorrectionLevel> hintMap;
    private final Path rootLocation;

    @Autowired
    public QrCodeGeneratorImpl(FileStorageProperties properties) {
        rootLocation = Paths.get(System.getProperty("user.home")
                + properties.getStorageLocation()).toAbsolutePath();
        log.info("rootLocation=" + rootLocation);
        if (!rootLocation.toFile().exists()) {
            log.info("lege Rootlokation an...");
            try {
                Files.createDirectory(rootLocation);
            } catch (IOException e) {
                throw new StorageException("Root-Directory kann nicht angelegt werden");
            }
        }
    }

    @PostConstruct
    private void init() {
        qrCodeWriter = new QRCodeWriter();
        hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
    }

    @Override
    public Path createQrCodeAsImageFile(String qrCodeUrl, String filename) {
        log.info("Erstelle QR-Code für URL {} unter Dateiname {}", qrCodeUrl, filename);
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 200, 200, hintMap);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            Path zielDatei = rootLocation.resolve(Paths.get(filename))
                    .normalize()
                    .toAbsolutePath();
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", zielDatei);
            log.info("Zieldatei erstellt: " + zielDatei);
            return zielDatei;
        } catch (WriterException | IOException e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }
}
