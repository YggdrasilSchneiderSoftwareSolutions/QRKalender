package de.qr.calendar.qrcode;

import java.nio.file.Path;

public interface QrCodeGenerator {
    Path createQrCodeAsImageFile(String qrCodeUrl, String filename);
}
