package de.qr.calendar.controller;

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
@RequestMapping(path = "/file")
public class FileController {

    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/upload/{eintragId}")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                   @PathVariable UUID eintragId) {
        log.info("Speichere Datei: {} für UUID {}", file.getOriginalFilename(), eintragId.toString());
        fileService.saveFile(eintragId, file);
        return ResponseEntity.ok("Datei erfolgreich gespeichert: " + file.getOriginalFilename());
    }

    @DeleteMapping(path = "/delete/{eintragId}/{filename}")
    public ResponseEntity<String> handleFileDelete(@PathVariable UUID eintragId, @PathVariable String filename) {
        String zielDatei = eintragId
                + FileSystems.getDefault().getSeparator()
                + filename;
        log.info("Lösche Datei: " + zielDatei);
        fileService.deleteFile(zielDatei);
        return ResponseEntity.ok("Datei erfolgreich gespeichert: " + filename);
    }

    @GetMapping("/download/{eintragId}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String eintragId,
                                                 @PathVariable String filename,
                                                 HttpServletRequest request) {
        String zielDatei = eintragId
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

    /*@GetMapping("/")
    public String message(Model model) {

        model.addAttribute("message", "Hallo Welt");

        return "fileupload";
    }*/
}
