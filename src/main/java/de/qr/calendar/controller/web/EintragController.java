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

import java.nio.file.FileSystems;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(path = "/eintrag")
public class EintragController {

    private EintragRepository eintragRepository;
    @Autowired
    private FileService fileService;

    @Autowired
    public EintragController(EintragRepository eintragRepository) {
        this.eintragRepository = eintragRepository;
    }

    @GetMapping("/qr")
    public String getEintragByScannedQrCode(@RequestParam(name = "id") UUID id, Model model) {
        log.info("QR-Code gescannt für Eintrag-ID {}", id);
        LocalDate heute = LocalDate.now();
        model.addAttribute("today", heute);

        Eintrag eintrag = eintragRepository.findById(id).get();
        model.addAttribute("eintrag", eintrag);
        model.addAttribute("kalender", eintrag.getKalender());

        String zielDatei = eintrag.getKalender().getId().toString()
                + FileSystems.getDefault().getSeparator()
                + eintrag.getBild();
        // Datei als Resource laden
        Resource resource = fileService.getFileAsResource(zielDatei);
        model.addAttribute("eintragImage", resource);

        return "eintrag";
    }
}
