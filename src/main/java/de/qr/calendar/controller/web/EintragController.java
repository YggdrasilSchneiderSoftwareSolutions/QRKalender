package de.qr.calendar.controller.web;

import de.qr.calendar.file.FileService;
import de.qr.calendar.model.Eintrag;
import de.qr.calendar.repository.EintragRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(path = "/eintrag")
public class EintragController {

    private EintragRepository eintragRepository;
    private FileService fileService;

    @Autowired
    public EintragController(EintragRepository eintragRepository, FileService fileService) {
        this.eintragRepository = eintragRepository;
        this.fileService = fileService;
    }

    @GetMapping("/qr")
    public String getEintragByScannedQrCode(@RequestParam(name = "id") UUID id, Model model) {
        log.info("QR-Code gescannt für Eintrag-ID {}", id);
        LocalDate heute = LocalDate.now();
        model.addAttribute("today", heute);

        Eintrag eintrag = eintragRepository.findById(id).orElseThrow();
        model.addAttribute("eintrag", eintrag);
        model.addAttribute("kalender", eintrag.getKalender());

        // Check, ob Bild überhaupt vorhanden, um FileNotFoundException zu verhindern
        if (eintrag.getBild() != null) {
            String zielDatei = eintrag.getKalender().getId().toString()
                    + FileSystems.getDefault().getSeparator()
                    + eintrag.getBild();
            // Datei als Resource laden
            Resource resource = fileService.getFileAsResource(zielDatei);
            // Datei in Base64-String konvertieren und Content-Type ermitteln
            String encodedString = "";
            String contentType = "";
            try {
                encodedString = Base64.getMimeEncoder()
                        .encodeToString(Files.readAllBytes(resource.getFile().toPath()));
                contentType = Files.probeContentType(resource.getFile().toPath());
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
            }

            model.addAttribute("imageContentType", contentType);
            model.addAttribute("eintragImage", encodedString);
        }

        return "eintrag";
    }
}
