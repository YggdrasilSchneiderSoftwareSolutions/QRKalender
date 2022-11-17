package de.qr.calendar.controller.rest;

import de.qr.calendar.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping(path = "/api/file")
public class FileRestController {

    private FileService fileService;

    @Autowired
    public FileRestController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/upload/{kalenderId}")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                   @PathVariable UUID kalenderId) {
        log.info("Speichere Datei: {} für UUID {}", file.getOriginalFilename(), kalenderId.toString());
        fileService.saveFile(kalenderId, file);
        return ResponseEntity.ok("Datei erfolgreich gespeichert: " + file.getOriginalFilename());
    }

    @DeleteMapping(path = "/delete/{kalenderId}/{filename}")
    public ResponseEntity<String> handleFileDelete(@PathVariable UUID kalenderId, @PathVariable String filename) {
        String zielDatei = kalenderId
                + FileSystems.getDefault().getSeparator()
                + filename;
        log.info("Lösche Datei: " + zielDatei);
        fileService.deleteFile(zielDatei);
        return ResponseEntity.ok("Datei erfolgreich gespeichert: " + filename);
    }

    @GetMapping("/download/{kalenderId}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String kalenderId,
                                                 @PathVariable String filename,
                                                 HttpServletRequest request) {
        String zielDatei = kalenderId
                + FileSystems.getDefault().getSeparator()
                + filename;
        // Datei als Resource laden
        Resource resource = fileService.getFileAsResource(zielDatei);
        log.info("Lade Datei " + resource.getFilename());

        // Content type ermitteln
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Content-type konnte nicht ermittelt werden");
        }

        // Fallback zum default content type
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + resource.getFilename() + "\"")
                .body(resource);
    }
}
