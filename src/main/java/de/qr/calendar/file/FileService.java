package de.qr.calendar.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileService {
    void saveFile(UUID eintragId, MultipartFile file);
    void deleteFile(String filename);
    Resource getFileAsResource(String filename);
}
