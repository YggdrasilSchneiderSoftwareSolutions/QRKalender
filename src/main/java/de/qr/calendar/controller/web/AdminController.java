package de.qr.calendar.controller.web;

import de.qr.calendar.file.FileService;
import de.qr.calendar.model.Eintrag;
import de.qr.calendar.model.Kalender;
import de.qr.calendar.repository.EintragRepository;
import de.qr.calendar.repository.KalenderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystems;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@Slf4j
public class AdminController {

    private KalenderRepository kalenderRepository;
    private EintragRepository eintragRepository;
    private FileService fileService;

    @Autowired
    public AdminController(KalenderRepository kalenderRepository, EintragRepository eintragRepository,
                           FileService fileService) {
        this.kalenderRepository = kalenderRepository;
        this.eintragRepository = eintragRepository;
        this.fileService = fileService;
    }

    @GetMapping("/admin")
    public String renderAdminPage(Model model) {
        Iterable<Kalender> allKalender = kalenderRepository.findAll();
        log.info("{} Kalender insgesamt gefunden", StreamSupport
                .stream(allKalender.spliterator(), false)
                .count());
        model.addAttribute("allKalender", allKalender);
        model.addAttribute("kalender", new Kalender());
        return "admin";
    }

    @PostMapping("/savekalender")
    public String saveKalender(@ModelAttribute Kalender kalender, Model model) {
        if (kalender.getId() != null) { // Kalender wird editiert
            kalenderRepository.findById(kalender.getId())
                    .map(kal -> {
                        kal.setId(kalender.getId());
                        kal.setEmpfaenger(kalender.getEmpfaenger());
                        kal.setBezeichnung(kalender.getBezeichnung());
                        kal.setGueltigVon(kalender.getGueltigVon());
                        kal.setGueltigBis(kalender.getGueltigBis());
                        return kalenderRepository.save(kal);
                    })
                    .orElseThrow();
            log.info("Kalender '{}' editiert", kalender.getBezeichnung());
        } else { // Neuer Kalender
            kalenderRepository.save(kalender);
            log.info("Kalender '{}' angelegt", kalender.getBezeichnung());
        }

        Iterable<Kalender> allKalender = kalenderRepository.findAll();
        model.addAttribute("allKalender", allKalender);
        model.addAttribute("kalender", new Kalender());
        return "admin";
    }

    @PostMapping("/deletekalender")
    public String deleteKalender(@RequestParam(name = "kalenderId") UUID kalenderId, Model model) {
        kalenderRepository.deleteById(kalenderId);
        log.info("Kalender-ID {} gelöscht", kalenderId);
        Iterable<Kalender> allKalender = kalenderRepository.findAll();
        model.addAttribute("allKalender", allKalender);
        model.addAttribute("kalender", new Kalender());
        return "admin";
    }

    @PostMapping("/editkalender")
    public String editKalender(@RequestParam(name = "kalenderId") UUID kalenderId, Model model) {
        Kalender kalenderToEdit = kalenderRepository.findById(kalenderId).orElseThrow();
        log.info("Kalender '{}' für Änderung gelesen", kalenderToEdit.getBezeichnung());
        Iterable<Kalender> allKalender = kalenderRepository.findAll();
        model.addAttribute("allKalender", allKalender);
        model.addAttribute("kalender", kalenderToEdit);
        return "admin";
    }

    @GetMapping("/eintraegekalender")
    public String getEintragsPageForKalender(@RequestParam(name = "kalenderId") UUID kalenderId, Model model) {
        Kalender kalender = kalenderRepository.findById(kalenderId).orElseThrow();
        model.addAttribute("kalender", kalender);
        Iterable<Eintrag> allEintraege = kalender.getEintraege()
                .stream()
                .sorted(Comparator.comparing(Eintrag::getNummer))
                .collect(Collectors.toList());

        log.info("{} Einträge zu Kalender '{}' gefunden", StreamSupport
                .stream(allEintraege.spliterator(), false)
                .count(), kalender.getBezeichnung());

        model.addAttribute("allEintraege", allEintraege);
        model.addAttribute("eintrag", new Eintrag());
        return "admin_eintraege";
    }

    @PostMapping("/saveeintrag")
    public String saveEintrag(@ModelAttribute Eintrag eintrag,
                              @RequestParam(name = "kalenderId") UUID kalenderId,
                              Model model) {
        Kalender selectedKalender = kalenderRepository.findById(kalenderId).orElseThrow();
        model.addAttribute("kalender", selectedKalender);
        eintrag.setKalender(selectedKalender);
        if (eintrag.getId() != null) { // Eintrag wird editiert
            eintragRepository.findById(eintrag.getId())
                    .map(e -> {
                        e.setKalender(eintrag.getKalender());
                        e.setNummer(eintrag.getNummer());
                        // Bild nicht setzen, sonst null (Bild hat eigene form!)
                        e.setInhalt(eintrag.getInhalt());
                        e.setLink(eintrag.getLink());
                        e.setAufrufbarAb(eintrag.getAufrufbarAb());
                        return eintragRepository.save(e);
                    })
                    .orElseThrow();

        } else { // Neuer Eintrag
            eintragRepository.save(eintrag);
        }

        log.info("Eintrag zu Kalender '{}' gespeichert", selectedKalender.getBezeichnung());

        Iterable<Eintrag> allEintraege = selectedKalender.getEintraege()
                .stream()
                .sorted(Comparator.comparing(Eintrag::getNummer))
                .collect(Collectors.toList());
        model.addAttribute("allEintraege", allEintraege);
        model.addAttribute("eintrag", new Eintrag());
        return "admin_eintraege";
    }

    @PostMapping("/deleteeintrag")
    public String deleteEintrag(@RequestParam(name = "eintragId") UUID eintragId,
                                @RequestParam(name = "kalenderId") UUID kalenderId,
                                Model model) {
        Eintrag eintragToDelete = eintragRepository.findById(eintragId).orElseThrow();
        // Bild löschen, wenn vorhanden
        if (eintragToDelete.getBild() != null) {
            String zielDatei = kalenderId.toString()
                    + FileSystems.getDefault().getSeparator()
                    + eintragToDelete.getBild();
            log.info("Lösche Datei '{}' zu Eintrag-ID {}", zielDatei, eintragId);
            fileService.deleteFile(zielDatei);
        }
        // Eintrag löschen
        eintragRepository.deleteById(eintragId);
        log.info("Eintrag-ID {} gelöscht", eintragId);

        Kalender kalender = kalenderRepository.findById(kalenderId).orElseThrow();
        model.addAttribute("kalender", kalender);
        Iterable<Eintrag> allEintraege = kalender.getEintraege()
                .stream()
                .sorted(Comparator.comparing(Eintrag::getNummer))
                .collect(Collectors.toList());
        model.addAttribute("allEintraege", allEintraege);
        model.addAttribute("eintrag", new Eintrag());
        return "admin_eintraege";
    }

    @PostMapping("/editeintrag")
    public String editEintrag(@RequestParam(name = "kalenderId") UUID kalenderId,
                              @RequestParam(name = "eintragId") UUID eintragId,
                              Model model) {
        Kalender kalender = kalenderRepository.findById(kalenderId).orElseThrow();
        Iterable<Eintrag> allEintraege = kalender.getEintraege()
                .stream()
                .sorted(Comparator.comparing(Eintrag::getNummer))
                .collect(Collectors.toList());
        Eintrag eintragToEdit = eintragRepository.findById(eintragId).orElseThrow();
        model.addAttribute("kalender", kalender);
        model.addAttribute("allEintraege", allEintraege);
        model.addAttribute("eintrag", eintragToEdit);
        return "admin_eintraege";
    }

    @PostMapping(path = "/uploadfile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam(name = "kalenderId") UUID kalenderId,
                                   @RequestParam(name = "eintragId") UUID eintragId,
                                   Model model) {
        // Datei in Ordner des Kalenders speichern
        log.info("Speichere Datei: {} für Kalender-ID {} zur Eintrag-ID{}",
                file.getOriginalFilename(), kalenderId.toString(), eintragId.toString());
        fileService.saveFile(kalenderId, file);
        // Dateiname in Eintrag speichern
        Eintrag eintrag = eintragRepository.findById(eintragId).orElseThrow();
        eintrag.setBild(file.getOriginalFilename());
        Eintrag updEintrag = eintragRepository.save(eintrag);

        Kalender kalender = kalenderRepository.findById(kalenderId).orElseThrow();
        model.addAttribute("kalender", kalender);
        Iterable<Eintrag> allEintraege = kalender.getEintraege()
                .stream()
                .sorted(Comparator.comparing(Eintrag::getNummer))
                .collect(Collectors.toList());
        model.addAttribute("allEintraege", allEintraege);
        model.addAttribute("eintrag", new Eintrag());
        return "admin_eintraege";
    }

    @PostMapping("/deletefile")
    public String deleteFile(@RequestParam(name = "eintragId") UUID eintragId, Model model) {
        Eintrag eintrag = eintragRepository.findById(eintragId).orElseThrow();
        Kalender kalender = eintrag.getKalender();
        // Datei auf dem Filesystem löschen
        String zielDatei = kalender.getId()
                + FileSystems.getDefault().getSeparator()
                + eintrag.getBild();
        log.info("Lösche Datei: " + zielDatei);
        fileService.deleteFile(zielDatei);
        // Datei am Eintrag in Datenbank löschen
        eintrag.setBild(null);
        eintragRepository.save(eintrag);

        model.addAttribute("kalender", kalender);
        Iterable<Eintrag> allEintraege = kalender.getEintraege()
                .stream()
                .sorted(Comparator.comparing(Eintrag::getNummer))
                .collect(Collectors.toList());
        model.addAttribute("allEintraege", allEintraege);
        model.addAttribute("eintrag", new Eintrag());
        return "admin_eintraege";
    }
}
